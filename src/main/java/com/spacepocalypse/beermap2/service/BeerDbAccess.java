package com.spacepocalypse.beermap2.service;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.domain.MappedValue;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

public class BeerDbAccess extends DbExecutor {
	private static final int DB_EXECUTE_RETRY_ATTEMPTS = 5;
	
	private static final String ID_KEY = "_id";

	private static final String SELECT_
		= "select beers.id, name, abv, descript, upc.upca ";

	private static final String SELECT_BY_NAME = 
		SELECT_ +
		"from beers left outer join upc on (beers.id = upc.beer_fk) " +
		"where lower(name) like ?";
	
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
	private BeerDbAccess(String username, String password) {
		super(username, password);
		log4jLogger = Logger.getLogger(BeerDbAccess.class);
	}
	
	public void close() throws SQLException {
		synchronized (dbConnection) {
			if (getDbConnection() != null && !getDbConnection().isClosed()) {
				getDbConnection().close();
			}
		}
	}
	
	public boolean insertBeer(MappedBeer beer) {
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
		
		return executeInsert(INSERT_BEER, params, paramTypes);
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
		
		if (parameters.containsKey(Constants.QUERY_KEY_BEER_ID)) {
			query.append("b.id = ? ");
			
			String beerIdString = parameters.get(Constants.QUERY_KEY_BEER_ID);
			
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
		if (username == null || username.trim().isEmpty() || hashPass == null || hashPass.trim().isEmpty()) {
			// return immediately under various conditions
			return null;
		}

		List<Object> values = new ArrayList<Object>();
		List<Class<?>> types = new  ArrayList<Class<?>>();

		values.add(StringUtils.lowerCase(username));
		types.add(String.class);

		values.add(hashPass);
		types.add(String.class);

		ResultSet rs = execute(SELECT_ALL_USER_AND_HASHPASS, values, types);
		
		MappedUser user = null;
		try {
			
			if (rs.next()) {
				user = new MappedUser();
				user.setId(rs.getInt(1));
				user.setUsername(rs.getString(2));
				user.setActive(rs.getInt(3) == 1);
			}
		} catch (SQLException e) {
			log4jLogger.error(
					Conca.t(
							"SQLException occurred while attempting to auth user:{",
							"username=[", username, "] hashPass=[", hashPass, "]"
					),
					e
			);
			closeAndReconnect();
			return null;
		}
		return user;
	}
	
	public List<MappedBeer> findAllBeers(Map<String, String[]> parameters) throws SQLException, InvalidParameterException {
		Map<String, Integer> queryKeyOrder = new HashMap<String, Integer>();
		int keyOrderIndex = 1;
		StringBuilder queryBuilder = new StringBuilder(SELECT_ALL_WHERE);
		
		if (parameters.containsKey(Constants.QUERY_KEY_NAME)) {	
			queryBuilder.append("lower(name) like ? ");
			queryKeyOrder.put(Constants.QUERY_KEY_NAME, keyOrderIndex++);
		} 
		
		if (parameters.containsKey(Constants.QUERY_KEY_ABV)) {
			if (keyOrderIndex > 1) {
				queryBuilder.append("and ");
			}
			queryBuilder.append("abv like ? ");
			queryKeyOrder.put(Constants.QUERY_KEY_ABV, keyOrderIndex++);
		} 
		
		if (parameters.containsKey(Constants.QUERY_KEY_UPC)) {
			if (keyOrderIndex > 1) {
				queryBuilder.append("and ");
			}
			queryBuilder.append("upc.upca like ? ");
			queryKeyOrder.put(Constants.QUERY_KEY_UPC, keyOrderIndex++);
		}
		
		if (keyOrderIndex <= 1) {
			throw new InvalidParameterException("No valid search keys specified"); 
		}
		
		ResultSet rs = execute(parameters, queryKeyOrder, queryBuilder.toString());
		
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
			
			if (log4jLogger.isDebugEnabled()) {
				log4jLogger.debug(Conca.t("executeInsert: Query=[", query, "]"));
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
					int parameterIndex = i+1;
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
			Map<String, String[]> parameters,
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
					ps.setString(index, "%" + parameters.get(key)[0] + "%");
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

	private static MappedBeer createMappedBeer(ResultSet rs) throws SQLException {
		int col = 1;
		MappedBeer beer = new MappedBeer();
		beer.setId(rs.getInt(col++));
		beer.setName(rs.getString(col++));
		beer.setAbv(rs.getFloat(col++));
		beer.setDescript(rs.getString(col++));
		beer.setUpc(rs.getString(col++));
		return beer;
	}
	
//	public static void main(String[] args) {
//		System.setProperty("log4j.configuration", "file:/home/dylan/workspace/beermap/web/WEB-INF/log4j.properties");
//		BeerDbAccess beerDb = new BeerDbAccess();
//		Map<String,String[]> params = new HashMap<String, String[]>();
//		params.put(BeerSearchEngine.QUERY_KEY_NAME, new String[]{"test_beer"});
//		List<MappedBeer> beers = null;
//		try {
//			beers = beerDb.findAllBeers(params);
//		} catch (InvalidParameterException e1) {
//			e1.printStackTrace();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		MappedBeer beer = beers.get(0);
//		System.out.println(beer.toString());
//		
//		List<MappedValue> ratingLVs = null;
//		ratingLVs = beerDb.findAllBeerRatingLegalValues();
//		System.out.println(ratingLVs.toString());
//		
//		MappedUser user = beerDb.findUserByUsername("dylan");
//		System.out.println("user=[" + user.getUsername() + "]");
//		
//		MappedBeerRating rating = new MappedBeerRating();
//		rating.setBeer(beer);
//		rating.setUser(user);
//		rating.setRating(ratingLVs.get(0));
//		rating.setComment("HaHA! A Comment!");
//		
//		System.out.println("Result: " + beerDb.insertRating(rating));
//		
//		Map<String, String[]> parameters = new HashMap<String, String[]>();
//		parameters.put(BeerSearchEngine.QUERY_KEY_BEER_ID, new String[]{String.valueOf(beer.getId())});
//		
//		System.out.println("findAllBeerRatings: " + beerDb.findAllBeerRatings(parameters));
//		
//	}
}
