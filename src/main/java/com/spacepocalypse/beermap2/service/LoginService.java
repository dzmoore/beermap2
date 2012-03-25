package com.spacepocalypse.beermap2.service;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.util.security.SimplePasswordTools;
import com.spacepocalypse.util.Conca;

public class LoginService {
private static long DEFAULT_AUTH_TIMEOUT_MS = 1000L * 60L * 60L * 24L * 30L;  // 30 days 
	
	private AtomicLong authTimeoutMs;
	private Logger log4jLogger;
	private BeerDbAccess dbAccess;
	
	private LoginService(BeerDbAccess dbAccess) {
		log4jLogger = Logger.getLogger(getClass());
		
		authTimeoutMs = new AtomicLong(DEFAULT_AUTH_TIMEOUT_MS);
		log4jLogger.info(Conca.t("Initialized authTimeoutMs to: ", authTimeoutMs.get(), "ms"));
		
		this.dbAccess = dbAccess;
	}
	
	public AuthData authUser(String username, String password) {
	    final AuthData errorAuthData = new AuthData(null, AuthState.ERROR, -1);
        AuthData data = errorAuthData;
        
	    // first hash calculate the password hash
        final String saltedHashPw = findSaltedPasswordHash(username, password);
        
        if (saltedHashPw != null) {
        
            final MappedUser user = getDbAccess().userAndPasswordMatch(username, saltedHashPw);
            
            if (user != null) {
                data = new AuthData(user, AuthState.SUCCESS, getAuthTimeoutMs());
            }
        }
        
		return data;
	}

	public boolean changePassword(final String username, String currentPassword, String newPassword) {
	    boolean success = false;
	    final MappedUser user = getDbAccess().findMappedUser(username);

	    if (user == null || user.getId() == -1) {
	        log4jLogger.warn(Conca.t("cannot find user [", username, "] while trying to change password"));

	    } else {
	        final String saltedPwHash = findSaltedPasswordHash(username, currentPassword);
	        if (saltedPwHash != null) { 
	            final MappedUser authUser = getDbAccess().userAndPasswordMatch(username, saltedPwHash);

	            if (authUser != null && authUser.isActive() && authUser.getId() != -1) {
	                final String salt = getDbAccess().findSalt(username);
	                String hashPw;

	                try {
	                    hashPw = SimplePasswordTools.hashPassAndSalt(newPassword, salt);
	                    success = getDbAccess().updateUserPassword(authUser.getId(), hashPw);
	                } catch (Exception e) {
	                    log4jLogger.warn(Conca.t("error occurred while attempting to hash pw for user [", username, "]"), e);
	                }
	            }
	        }
	    }

	    return success;
	}
	
	public MappedUser createUser(String username, String password) {
	    MappedUser user = new MappedUser();
	    if (getDbAccess().findMappedUser(username).getId() == -1) {
	        final String salt = getUniqueSalt();
	        String hashPass = null;
	        try {
	            hashPass = SimplePasswordTools.hashPassAndSalt(password, salt);
	            user = getDbAccess().createUser(username, salt, hashPass);

	        } catch (Exception e) {
	            log4jLogger.error(Conca.t("error creating user [", username, "]"), e);
	        }
	    }

	    return user;
	}

	public String getUniqueSalt() {
	    String salt = SimplePasswordTools.getSalt();

	    // find a unique salt
	    while (!getDbAccess().saltDoesNotExist(salt)) {
	        salt = SimplePasswordTools.getSalt();
	    }

	    return salt;
	}

	private String findSaltedPasswordHash(String username, String password) {
	    String saltedHashPw = null;
	    final String salt = getDbAccess().findSalt(username);
      
        if (salt == null) {
            log4jLogger.warn(Conca.t("salt for user[", username, "] is null"));
        
        } else {
            try {
                saltedHashPw = SimplePasswordTools.hashPassAndSalt(password, salt);
            
            } catch (Exception e) {
                log4jLogger.warn(Conca.t("There was a problem hashing the password and salt for user [", username, "]"), e);
            }
        }
        return saltedHashPw;
    }
	
	public BeerDbAccess getDbAccess() {
		return dbAccess;
	}

	public void setAuthTimeoutMs(long authTimeoutMs) {
		this.authTimeoutMs.set(authTimeoutMs);
	}

	public long getAuthTimeoutMs() {
		return authTimeoutMs.get();
	}
	
	public class AuthData {
		private final long authTimeout;
		private final MappedUser user;
		private final AuthState state;
		
		public AuthData(MappedUser user, AuthState state, long authTimeout) {
			this.authTimeout = authTimeout;
			this.user = user;
			this.state = state;
		}
		
		public long getAuthTimeoutMs() {
			return authTimeout;
		}
		public MappedUser getUser() {
			return user;
		}

		public AuthState getState() {
			return state;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("user=[");
			sb.append(getUser());
			sb.append("] authTimeoutMs=[");
			sb.append(getAuthTimeoutMs());
			sb.append("] state=[");
			sb.append(getState().toString());
			sb.append("]");
			return sb.toString();
		}
	}
	
	public enum AuthState {
		SUCCESS,
		ERROR
	}
}
