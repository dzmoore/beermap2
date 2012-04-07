package com.spacepocalypse.beermap2.dao;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedBrewery;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.domain.MappedValue;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

public class BeerDbAccess extends DbExecutor {
	private static final int DB_EXECUTE_RETRY_ATTEMPTS = 5;
	
	private static final String ID_KEY = "_id";

	private static final String SELECT_
		= "select beers.id, name, abv, descript, upc.upca ";

	private static final String SELECT_BY_NAME = 
		"select " +
		"	b.id as beer_id, " +
		"	b.name as beer_name, " +
		"	b.abv as beer_abv, " +
		"	b.descript as beer_descript, " +
		"	u.upca as upc_upca, " +
		"	br.id as brewery_id, " +
		"	br.name as brewery_name, " +
		"	br.country as brewery_country, " +
		"	br.descript as brewery_descript " +
		"from " +
		"	beers b left outer join upc u on (b.id = u.beer_fk) " +
		" 	left outer join breweries br on (b.brewery_id = br.id) " +
		"where ";
	
	private static final String SELECT_BY_UPC =
		SELECT_ +
		"from beers join upc on (beers.id = upc.beer_fk) " +
		"where upc.upca = ?";

	private static final String SELECT_ALL_WHERE = 
		SELECT_ +
		"from beers left outer join upc on (beers.id = upc.beer_fk) " +
		"where ";
	
	private static final String SELECT_ALL_USER = 
		"select id, username, active from user " +
		"where lower(username) = ?";
	
	private static final String SELECT_ALL_USER_AND_HASHPASS = 
		SELECT_ALL_USER +
		" and password = ?";
	
	private static final String UPDATE_WHERE_ID = 
		"update beers " +
		"set name=?, abv=?, descript=? " +
		"where id=?";
	
	private static final String UPDATE_RATING = 
		"update beer_rating " +
		"set rating_fk = ?, comment = ? " +
		"where user_fk = ? and beer_fk = ?";
	
	private static final String FIND_ALL_RATINGS_FOR_BEER = 
		"select " +
		  "b.id, b.name, b.abv, b.descript, " +
		  "u.id, u.username, u.active, " +
		  "br.id, br.comment, " +
		  "brLV.rating, brLV.value " +
		"from " +
		  "beer_rating br join user u on (br.user_fk = u.id) " +
		  "join beers b on (br.beer_fk = b.id) " +
		  "join beer_rating_LV brLV on (br.rating_fk = brLV.id) where ";
	
	private static final String INSERT_BEER_RATING = 
		"insert into beer_rating " +
		  "(user_fk, beer_fk, rating_fk, comment) " +
		"values " +
		  "(?, ?, ?, ?)";
	
	private static final String INSERT_BEER = 
		"insert into beers " +
		"  (name, abv, descript)" +
		"values" +
		"  (?, ?, ?)";
	
	private static final String FIND_ALL_BEER_RATING_LEGAL_VALUES = 
		"select id, rating, value " +
		"from beer_rating_LV";
	private Logger log4jLogger;
	public BeerDbAccess(String dbName, String username, String password) {
		super(dbName, username, password);
		log4jLogger = Logger.getLogger(BeerDbAccess.class);
	}
	
    public String findSalt(final String username) {
        String salt = null;
        final String query = Conca.t(
                "select salt ",
                "from user ",
                "where lower(username) = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        try {
            ResultSet rs = execute(query, values, types);
            if (rs.next()) {
                salt = rs.getString("salt");
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find salt for username [", username, "]"), e);
        }
        
        return salt;
    }
	
	public void close() throws SQLException {
		synchronized (dbConnection) {
			if (getDbConnection() != null && !getDbConnection().isClosed()) {
				getDbConnection().close();
			}
		}
	}
	
	public boolean insertBeer(MappedBeer beer, int userId) {
		if (beer == null || beer.getName() == null || beer.getName().trim().isEmpty()) {
			return false;
		}
		
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		
		params.add(beer.getName());
		paramTypes.add(String.class);
		
		params.add(beer.getAbv());
		paramTypes.add(Float.class);
		
		params.add(beer.getDescript());
		paramTypes.add(String.class);
		
		params.add(userId);
		paramTypes.add(Integer.class);
		
		final StringBuilder queryBuilder = new StringBuilder("insert into beers ");
		queryBuilder.append("(name, abv, descript, add_user_fk");

		if (beer.getBrewery() != null && beer.getBrewery().getId() != Constants.INVALID_ID) {
		    queryBuilder.append(", brewery_id) values (?, ?, ?, ?, ?)");
		    
		    params.add(beer.getBrewery().getId());
		    paramTypes.add(Integer.class);
		
		} else {
		    queryBuilder.append(") values (?, ?, ?, ?)");
		}
		
		return executeInsert(queryBuilder.toString(), params, paramTypes);
	}
	
    public MappedUser findMappedUser(final String username) {
        final String query = SELECT_ALL_USER;
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        MappedUser user = new MappedUser();
        try {
            ResultSet rs = execute(query, values, types);
            if (rs.next()) {
                user = MappedUser.createMappedUser(rs);
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find user for username [", username, "]"), e);
        }
        
        return user;
    }
    
    public MappedUser createUser(final String username, final String salt, final String password) {
        final String query = Conca.t(
            "insert into user ",
            "(username, active, salt, password) ",
            "VALUES ",
            "(?, ?, ?, ?)"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        values.add(1);
        types.add(Integer.class);
        
        values.add(salt);
        types.add(String.class);
        
        values.add(password);
        types.add(String.class);
        
        MappedUser user = new MappedUser();
        try {
            int rows = executeUpdate(query, values, types);
            if (rows == 1) {
                user = findMappedUser(username);
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to create user for username [", username, "]"), e);
        }
        
        return user;
    }
    
    public boolean saltDoesNotExist(final String salt) {
        final String query = Conca.t(
                "select salt ",
                "from user ",
                "where salt = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(salt);
        types.add(String.class);
        
        try {
            ResultSet rs = execute(query, values, types);

            return !rs.next();
        } catch (Exception e) {
            log4jLogger.error("error occurred while attempting to find salt", e);
        }
        
        return false;
    }
    
    public boolean updateUserPassword(final int userId, final String newHashPassword) {
        boolean success = false;
        final String query = Conca.t(
            "update user ",
            "set password = ? ",
            "where id = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(newHashPassword);
        types.add(String.class);
        
        values.add(userId);
        types.add(Integer.class);
        
        try {
            int rows = executeUpdate(query, values, types);
            success = (rows == 1);
        
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to change password for userid [", userId, "]"), e);
        }
        
        return success;
    }
	
	public boolean updateById(MappedBeer beer) {
		if (beer == null) {
			return false;
		}
		try {
			PreparedStatement ps = getDbConnection().prepareStatement(UPDATE_WHERE_ID);
			ps.setString(1, beer.getName());
			ps.setFloat(2, beer.getAbv());
			ps.setString(3, beer.getDescript());
			ps.setInt(4, beer.getId());
			int result = ps.executeUpdate();
			return result > 0;
			
		} catch (SQLException e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to update beer [", beer, "]"), e);
		} 
		return false;
	}
	
	public boolean updateBeerRating(MappedBeerRating rating) {
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		
		params.add(rating.getRating().getId());
		paramTypes.add(Integer.class);
		
		params.add(rating.getComment());
		paramTypes.add(String.class);
		
		params.add(rating.getUser().getId());
		paramTypes.add(Integer.class);
		
		params.add(rating.getBeer().getId());
		paramTypes.add(Integer.class);
		
		int result = executeUpdate(UPDATE_RATING, params, paramTypes);
		
		return result > 0;
	}
	
	private int executeUpdate(
			String query, 
			List<Object> params, 
			List<Class<?>> paramTypes) 
	{
		int rowsUpdated = -1;
		int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
		while (retryAttempts-- > 0) {
			if (log4jLogger.isDebugEnabled()) {
				log4jLogger.debug("executeInsert: Query=[" + query + "]");
			}
			
			PreparedStatement ps = null;
			
			try {
				ps = getDbConnection().prepareStatement(query);
			} catch (Exception e) {
				log4jLogger.error("Error while trying to prepare statement for query: [: " + query + "]", e);
				if (e instanceof SQLException) {
					break;
				}

				// SocketException?
				if (e.getMessage().toLowerCase().contains("broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
					continue;
				}
			}
			
			for (int i = 0; i < params.size(); i++) {
				Object ea = params.get(i);
				Class<?> eaType = paramTypes.get(i);

				if (ea == null || eaType == null) {
					log4jLogger.error("parameter(s) is null! params=[" + params.toString() + "], paramTypes=[" + paramTypes.toString() + "]");
					return rowsUpdated;
				}
				
				try {
					// parameters are one-based indexes...
					int parameterIndex = i+1;
					
					setParamByType(ps, ea, eaType, parameterIndex);
				} catch (SQLException e) {
					log4jLogger.error("SQLException while attempting to set parameter: [" + ea.toString() + "]", e);
					return rowsUpdated;
				}
			}

			try {			
				rowsUpdated = ps.executeUpdate();
				break;
			} catch (Exception e) {
				log4jLogger.error("Error when calling executeUpdate. preparedStatement=[" + (ps == null ? "NULL" : ps.toString()) + "]", e);

				if (e.getMessage().toLowerCase().contains("broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
				}
			}
		}
		return rowsUpdated;
	}

	private void setParamByType(PreparedStatement ps, Object ea,
			Class<?> eaType, int parameterIndex) throws SQLException {
		if (eaType == String.class) {
			ps.setString(parameterIndex, ea.toString());
			
		} else if (eaType == Integer.class) {
			ps.setInt(parameterIndex, (Integer)ea);
		
		} else if (eaType == Float.class) {
			ps.setFloat(parameterIndex, (Float)ea);
		}
	}
	
	public List<MappedValue> findAllBeerRatingLegalValues() {
		try {
			ResultSet results = execute(FIND_ALL_BEER_RATING_LEGAL_VALUES);
			List<MappedValue> retArr = new ArrayList<MappedValue>();
			while (results.next()) {
				retArr.add(MappedValue.createMappedValue(results));
			}
			return retArr;
		} catch (SQLException e) {
			e.printStackTrace();
			log4jLogger.error("findAllBeerRatingLegalValues(): " + e.getMessage());
			return new ArrayList<MappedValue>();
		}
	}
	
	public List<MappedBeerRating> findAllBeerRatings(Map<String, String> parameters) {
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		StringBuilder query = new StringBuilder(FIND_ALL_RATINGS_FOR_BEER);
		List<MappedBeerRating> ratings = new ArrayList<MappedBeerRating>();
		
		if (parameters.containsKey(Constants.KEY_BEER_ID)) {
			query.append("b.id = ? ");
			
			String beerIdString = parameters.get(Constants.KEY_BEER_ID);
			
			if (beerIdString == null) {
				log4jLogger.error(Conca.t("BeerId parameter is null. Parameters: [", parameters.toString(), "]"));
				return ratings;
			}
			
			try {
				params.add(Integer.parseInt(beerIdString));
				
			} catch (NumberFormatException e) {
				log4jLogger.error(Conca.t("Error parsing beerId. Parameters: [", parameters.toString(), "]"), e);
				return ratings;
			}
			
			paramTypes.add(Integer.class);
		}
		
		if (parameters.containsKey(Constants.QUERY_KEY_USER_ID)) {
			if (params.size() >= 1) {
				query.append(" and ");
			}
			
			query.append("u.id = ? ");
			String userIdString = parameters.get(Constants.QUERY_KEY_USER_ID);
			
			if (userIdString == null) {
				log4jLogger.error(Conca.t("UserId parameter is null. Parameters: [", parameters.toString(), "]"));
				return ratings;
			}
			
			try {
				params.add(Integer.parseInt(userIdString));
				
			} catch (NumberFormatException e) {
				log4jLogger.error(Conca.t("Error parsing userId. Parameters: [", parameters.toString(), "]"), e);
				return ratings;
			}
			paramTypes.add(Integer.class);
		}
		
		ResultSet results = execute(query.toString(), params, paramTypes);
		
		try {
			while (results != null && results.next()) {
				ratings.add(MappedBeerRating.createMappedBeerRating(results));
			}
			
		} catch (Exception e) {
			log4jLogger.error("Error occurred while attempting to create beer rating list from db.", e);
		}
		
		return ratings;
	}
	
	public boolean insertRating(MappedBeerRating rating) {
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		
		params.add((Integer)rating.getUser().getId());
		paramTypes.add(Integer.class);
		
		params.add((Integer)rating.getBeer().getId());
		paramTypes.add(Integer.class);
		
		params.add((Integer)rating.getRating().getId());
		paramTypes.add(Integer.class);
		
		params.add(rating.getComment());
		paramTypes.add(String.class);
		
 		return executeInsert(INSERT_BEER_RATING, params, paramTypes);
	}
	
	public List<MappedBeer> findBeersByIds(int[] ids) {
	    StringBuilder query = new StringBuilder(SELECT_BY_NAME);
	    
	    query.append("b.id in (");
	    
	    final List<Object> params = new ArrayList<Object>();
	    final List<Class<?>> paramTypes = new ArrayList<Class<?>>();
	    
	    boolean notFirst = false;
	    for (final int ea : ids) {
	        if (notFirst) {
	            query.append(", ");
	        }
	        
	        query.append("?");
	        
	        params.add(ea);
	        paramTypes.add(Integer.class);
	        
	        notFirst = true;
	    }

	    query.append(")");

	    final ResultSet rs = execute(query.toString(), params, paramTypes);

	    final List<MappedBeer> beers = new ArrayList<MappedBeer>();

	    try {
	        while (rs.next()) {
	            beers.add(createMappedBeer(rs));
	        }
	        
	    } catch (Exception e) {
	        log4jLogger.error("Error occurred while attemping to build beers for find beers by id", e);
	    }
	    
	    return beers;
	}
	
	private boolean executeInsert(String query, List<Object> params, List<Class<?>> paramTypes) {
		boolean success = false;
		int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
		while (retryAttempts-- > 0) {
			if (log4jLogger.isDebugEnabled()) {
				log4jLogger.debug("executeInsert: Query=[" + query + "]");
			}
			
			PreparedStatement ps = null;
			
			try {
				ps = getDbConnection().prepareStatement(query);
			} catch (Exception e) {
				log4jLogger.error("executeInsert(String, List<Object>, List<Class<?>>): " + e.getMessage());
				if (e instanceof SQLException) {
					break;
				}

				if (e.getMessage().toLowerCase().contains("broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
					continue;
				}
			}
			
			for (int i = 0; i < params.size(); i++) {
				Object ea = params.get(i);
				Class<?> eaType = paramTypes.get(i);

				if (ea == null || eaType == null) {
					log4jLogger.error("executeInsert(String, List<Object>, List<Class<?>>): parameter is null!");
					return false;
				}
				
				try {
					int parameterIndex = i+1;
					setParamByType(ps, ea, eaType, parameterIndex);
				} catch (SQLException e) {
					log4jLogger.error("executeInsert(String, List<Object>, List<Class<?>>): " + e.getMessage());
					return false;
				}
			}

			try {			
				success = ps.executeUpdate() > 0;
				break;
			} catch (Exception e) {
				log4jLogger.error("executeInsert(String, List<Object>, List<Class<?>>): " + e.getMessage());

				if (e.getMessage().toLowerCase().contains("broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
				}
			}
		}
		return success;
	}

	public List<MappedBrewery> findAllBreweries() {
	    final String findAllBreweriesQuery = Conca.t(
	            "select ",
	            "  br.id as brewery_id, ",
	            "  br.name as brewery_name, ",
	            "  br.country as brewery_country, ",
	            "  br.descript as brewery_descript ",
	            "from breweries br"
	    );

	    final List<MappedBrewery> breweries = new ArrayList<MappedBrewery>();

	    try {
	        final ResultSet rs = execute(findAllBreweriesQuery, Collections.emptyList(), new ArrayList<Class<?>>());

	        while (rs.next()) {
	            breweries.add(createMappedBrewery(rs));
	        }
	        
        } catch (Exception e) {
            log4jLogger.error("Error occurred while attempting to find all breweries", e);
        }
	    
	    return breweries;
	}

	public MappedUser findUserByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}
		MappedUser user = null;
		try {
			PreparedStatement ps = getDbConnection().prepareStatement(SELECT_ALL_USER);
			ps.setString(1, username);
			ps.execute();
			
			ResultSet resultSet = ps.getResultSet();
			if (resultSet.next()) {
				user = MappedUser.createMappedUser(resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return user;
	}
	
	public MappedUser userAndPasswordMatch(String username, String hashPass) {
        if (username == null || username.trim().isEmpty() || password == null
                || password.trim().isEmpty()) {
            // return immediately under various conditions
            return null;
        }

        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);

        values.add(hashPass);
        types.add(String.class);

        ResultSet rs = execute(SELECT_ALL_USER_AND_HASHPASS, values, types);

        MappedUser user = null;
        try {

            if (rs.next()) {
                user = new MappedUser();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setActive(rs.getInt("active") == 1);
            }
        } catch (SQLException e) {
            log4jLogger.error(Conca.t(
                    "SQLException occurred while attempting to auth user:{",
                    "username=[", username, "]"), e);
            closeAndReconnect();
            return null;
        }
        return user;
	}
	
	public MappedBeer findBeerById(final String id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.KEY_BEER_ID, id);
		
		List<MappedBeer> results = Collections.emptyList();
		try {
			results = findAllBeers(params);
		
		} catch (Exception e) {
			log4jLogger.error(Conca.t("An error occurred while attempting to find beer by id. id:[", id, "]"),  e);
		}
		
		if (results.size() != 1) {
			log4jLogger.warn(Conca.t("Expected result size of one, received:{", results.toString(), "}"));
			return new MappedBeer();
		}
		
		return results.get(0);
	}
	
	public List<MappedBeer> findAllBeers(Map<String, String> parameters) throws SQLException, InvalidParameterException {
	    log4jLogger.trace(Conca.t("findAllBeers: parameters={", parameters, "}"));
	    
		final List<Object> params = new ArrayList<Object>();
		final List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		final StringBuilder query = new StringBuilder(SELECT_BY_NAME);
		final List<MappedBeer> beers = new ArrayList<MappedBeer>();
		
		if (parameters.containsKey(Constants.QUERY_KEY_BEER_NAME)) {
			query.append("lower(b.name) like ? ");
			
			String name = parameters.get(Constants.QUERY_KEY_BEER_NAME);
			
			if (name == null) {
				log4jLogger.error(Conca.t("beer name parameter is null. Parameters: [", parameters.toString(), "]"));
				return beers;
			}
			
			params.add(name);

			paramTypes.add(String.class);
		}
		
		if (parameters.containsKey(Constants.QUERY_KEY_BEER_OR_BREWERY)) {
            if (params.size() > 0) {
                query.append(" and ");
            }
            
            query.append("(lower(br.name) like ? or lower(b.name) like ?) ");

            String name = parameters.get(Constants.QUERY_KEY_BEER_OR_BREWERY);

            if (name == null) {
                log4jLogger.error(Conca.t("brewery name parameter is null. Parameters: [", parameters.toString(), "]"));
                return beers;
            }

            params.add(name);

            paramTypes.add(String.class);
            
            params.add(name);

            paramTypes.add(String.class);
        }

		if (parameters.containsKey(Constants.QUERY_KEY_BREWERY_NAME)) {
		    if (params.size() > 0) {
                query.append(" and ");
            }
		    
		    query.append("lower(br.name) like ? ");

		    String name = parameters.get(Constants.QUERY_KEY_BREWERY_NAME);

		    if (name == null) {
		        log4jLogger.error(Conca.t("brewery name parameter is null. Parameters: [", parameters.toString(), "]"));
		        return beers;
		    }

		    params.add(name);

		    paramTypes.add(String.class);
		}

		if (parameters.containsKey(Constants.QUERY_KEY_ABV)) {
			if (params.size() > 0) {
				query.append(" and ");
			}
			
			query.append("b.abv like ? ");
			
			final String abvStr = parameters.get(Constants.QUERY_KEY_ABV);
			
			try {
				float abvFloat = Float.parseFloat(abvStr);
				params.add(abvFloat);
				paramTypes.add(Float.class);
				
			} catch (Exception e) {
				log4jLogger.error(Conca.t("Error parsing float from abv [", abvStr, "]"), e);
				return beers;
			}
		}
		
		if (parameters.containsKey(Constants.QUERY_KEY_UPC)) {
			if (params.size() > 0) {
				query.append(" and ");
			}
			
			query.append("u.upca like ? ");
			
			final String upcStr = parameters.get(Constants.QUERY_KEY_UPC);
			
			params.add(upcStr);
			paramTypes.add(String.class);
		}
		
		if (parameters.containsKey(Constants.KEY_BEER_ID)) {
			if (params.size() > 0) {
				query.append(" and ");
			}

			final String idStr = parameters.get(Constants.KEY_BEER_ID);
			try {
				final int id = Integer.parseInt(idStr);
				params.add(id);
				paramTypes.add(Integer.class);
				
			} catch (Exception e) {
				log4jLogger.error(Conca.t("Error parsing int for id [", idStr, "]"), e);
				return beers;
			}
			
			query.append("b.id = ? ");
			paramTypes.add(Integer.class);
		}
		
		if (params.size() <= 0) {
			throw new InvalidParameterException("No valid search keys specified"); 
		}
		
		ResultSet rs = execute(query.toString(), params, paramTypes);
		
		List<MappedBeer> results = new ArrayList<MappedBeer>();
		while (rs.next()) {
			results.add(createMappedBeer(rs));
		}
		
		if (log4jLogger.isTraceEnabled()) {
			log4jLogger.trace(StrUtl.trunc(Conca.t("Results=[", results.toString(), "]"), 500));
		}
		
		return results;
	}
	
	private ResultSet execute(String query) throws SQLException {
		return execute(null, null, query);
	}
	
	private ResultSet execute(String query, List<Object> params, List<Class<?>> paramTypes) {
		ResultSet results = null;
		int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
		while (retryAttempts-- > 0) {
			
			if (log4jLogger.isTraceEnabled()) {
				log4jLogger.trace(Conca.t("execute: Query=[", query, "] params=[", params.toString(), "] paramTypes=[", paramTypes.toString(), "]"));
			}
			
			PreparedStatement ps = null;
			
			try {
				ps = getDbConnection().prepareStatement(query);
				
			} catch (Exception e) {
				// attempt to handle "broken pipe" and the like errors
				
				log4jLogger.error(Conca.t(
						"execute(String query, List<Object> params, List<Class<?>> paramTypes) errored: [", e.getMessage(), "]"), 
						e
				);
				
				if (e instanceof SQLException) {
					break;
				}

				if (StringUtils.containsIgnoreCase(e.getMessage(), "broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
					continue;
				}
			}
			
			for (int i = 0; i < params.size(); i++) {
				Object ea = params.get(i);
				Class<?> eaType = paramTypes.get(i);

				if (ea == null || eaType == null) {
					log4jLogger.error("execute(String query, List<Object> params, List<Class<?>> paramTypes): parameter is null!");
					return null;
				}
				
				try {
					final int parameterIndex = i+1;
					setParamByType(ps, ea, eaType, parameterIndex);
					
				} catch (Exception e) {
					log4jLogger.error(
							Conca.t("execute(String query, List<Object> params, List<Class<?>> paramTypes): [", e.getMessage(), "]"), 
							e
					);
					return null;
				}
			}

			try {			
				ps.execute();
				results = ps.getResultSet();
				break;
				
			} catch (Exception e) {
				log4jLogger.error(
						Conca.t("execute(String query, List<Object> params, List<Class<?>> paramTypes): [", e.getMessage(), "]"), 
						e
				);

				if (StringUtils.containsIgnoreCase(e.getMessage(), "broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
				}
			}
		}
		return results;
	}

	private ResultSet execute(
			Map<String, String> parameters,
			Map<String, Integer> queryKeyOrder,
			String query) throws SQLException 
	{
		int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
		PreparedStatement ps = null;
		while (retryAttempts-- > 0) {
			log4jLogger.debug("Query=[" + query + "]");
			ps = getDbConnection().prepareStatement(query);
			
			if (parameters != null && queryKeyOrder != null) {
				for (String key : queryKeyOrder.keySet()) {
					int index = queryKeyOrder.get(key);
					ps.setString(index, Conca.t("%", parameters.get(key), "%"));
				}
			}

			try {
				ps.execute(); 
				break;
			} catch (Exception e) {
				log4jLogger.error("execute(Map<String,String[]>, PreparedStatement, Map<String, Integer>, String): " + e.getMessage());

				if (e.getMessage().toLowerCase().contains("broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
				}
			}
		}
		if (ps == null || ps.getResultSet() == null) {
			return null;
		}
		return ps.getResultSet();
	}
	
	
	public List<MappedBeer> findAllBeersByName(String name)  {
		PreparedStatement ps = null;

		try {
			ps = getDbConnection().prepareStatement(SELECT_BY_NAME);
			ps.setString(1, "%" + name + "%");
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<MappedBeer>();
		}

		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<MappedBeer>();
		}

		List<MappedBeer> results = new ArrayList<MappedBeer>();
		ResultSet rs = null;
		try {
			rs = ps.getResultSet();
			while (rs.next()) {
				results.add(createMappedBeer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return results;
	}
	
	public List<MappedBeer> findAllBeersByUpc(String upc) {
		PreparedStatement ps = null;

		try {
			ps = getDbConnection().prepareStatement(SELECT_BY_UPC);
			ps.setString(1, upc);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<MappedBeer>();
		}

		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<MappedBeer>();
		}

		List<MappedBeer> results = new ArrayList<MappedBeer>();
		ResultSet rs = null;
		try {
			rs = ps.getResultSet();
			while (rs.next()) {
				results.add(createMappedBeer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return results;
	}
	
	private static MappedBrewery createMappedBrewery(ResultSet rs) throws SQLException {
	    final MappedBrewery br = new MappedBrewery();
	    br.setName(rs.getString("brewery_name"));
	    br.setCountry(rs.getString("brewery_country"));
	    br.setDescript(rs.getString("brewery_descript"));
	    br.setId(rs.getInt("brewery_id"));
	    return br;
	}

	private static MappedBeer createMappedBeer(ResultSet rs) throws SQLException {
		MappedBeer beer = new MappedBeer();
		beer.setId(rs.getInt("beer_id"));
		beer.setName(rs.getString("beer_name"));
		beer.setAbv(rs.getFloat("beer_abv"));
		beer.setDescript(rs.getString("beer_descript"));
		
		beer.setUpc(rs.getString("upc_upca"));
		
		beer.getBrewery().setName(rs.getString("brewery_name"));
		beer.getBrewery().setCountry(rs.getString("brewery_country"));
		beer.getBrewery().setDescript(rs.getString("brewery_descript"));
		beer.getBrewery().setId(rs.getInt("brewery_id"));
		
		Logger.getLogger(BeerDbAccess.class).trace(Conca.t("createMappedBeer result[", beer, "] "));
		return beer;
	}

    public List<MappedBrewery> findAllBreweries(String query) {
        final String findBreweriesQuery = Conca.t(
                "select ",
                "  br.id as brewery_id, ",
                "  br.name as brewery_name, ",
                "  br.country as brewery_country, ",
                "  br.descript as brewery_descript ",
                "from breweries br ",
                "where lower(br.name) like ?"
        );

        final List<MappedBrewery> breweries = new ArrayList<MappedBrewery>();

        final List<Object> params = new ArrayList<Object>();
        params.add(query);
        
        final List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(String.class);
        
        try {
            final ResultSet rs = execute(findBreweriesQuery, params, types);

            while (rs.next()) {
                breweries.add(createMappedBrewery(rs));
            }
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t("Error occurred while attempting to find breweries for query [", query, "]"), e);
        }
        
        return breweries;
    }
    
    public List<MappedBeer> findBeersForUserId(final int userId) {
        final String query = Conca.t(
            "select ", 
            "   b.id as beer_id, ",
            "   b.name as beer_name, ",
            "   b.abv as beer_abv, ", 
            "   b.descript as beer_descript, ", 
            "   u.upca as upc_upca, ", 
            "   br.id as brewery_id, ", 
            "   br.name as brewery_name, ", 
            "   br.country as brewery_country, ", 
            "   br.descript as brewery_descript ", 
            "from ", 
            "   beers b left outer join upc u on (b.id = u.beer_fk) ", 
            "   left outer join breweries br on (b.brewery_id = br.id) ",
            "   left outer join beer_rating rating on (b.id = rating.beer_fk) ",
            "where ",
            "   rating.user_fk = ? or ",
            "   b.add_user_fk = ? ",
            "order by rating.last_mod desc"
        );
        
        final List<Object> params = new ArrayList<Object>();
        params.add(userId);
        params.add(userId);
        
        final List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(Integer.class);
        types.add(Integer.class);
        
        final List<MappedBeer> beers = new ArrayList<MappedBeer>();
        
        try {
            final ResultSet rs = execute(query, params, types);

            while (rs.next()) {
                beers.add(createMappedBeer(rs));
            }
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t("Error occurred while attempting to find beers for user [", userId, "] for query [", query, "]"), e);
        }
        
        return beers;
    }
	
}
