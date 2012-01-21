package com.spacepocalypse.beermap2.service;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.domain.MappedUser;
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
	
	public AuthData authUser(String username, String hashPass) {
		MappedUser user = getDbAccess().userAndPasswordMatch(username, hashPass);
		AuthData data = null;
		
		if (user == null) {
			data = new AuthData(user, AuthState.ERROR, -1);
			
		} else {
			data = new AuthData(user, AuthState.SUCCESS, getAuthTimeoutMs());
		}
		
		return data;
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
