package com.spacepocalypse.beermap2.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;

public class MappedBeerRating implements Serializable {
	private static final long serialVersionUID = -2444895024305314779L;
	private MappedUser user;
	private MappedBeer beer;
	private MappedValue rating;
	private String comment;
	private int id;
	
	public MappedBeerRating() {
		setUser(new MappedUser());
		setBeer(new MappedBeer());
		setRating(new MappedValue());
		setComment("");
		setId(-1);
	}
	
	
	public void setUser(MappedUser user) {
		this.user = user;
	}
	public MappedUser getUser() {
		return user;
	}
	public void setBeer(MappedBeer beer) {
		this.beer = beer;
	}
	public MappedBeer getBeer() {
		return beer;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public void setRating(MappedValue rating) {
		this.rating = rating;
	}


	public MappedValue getRating() {
		return rating;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getComment() {
		return comment;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(":{id=[");
		sb.append(getId());
		sb.append("] comment=[");
		sb.append(getComment());
		sb.append("] rating=[");
		sb.append(getRating().toString());
		sb.append("] user=[");
		sb.append(user.getUsername());
		sb.append("] beer=[");
		sb.append(getBeer().toString());
		sb.append("]}");
		return sb.toString();
	}
	
	public static MappedBeerRating createMappedBeerRating(String jsonString) throws JSONException {
		MappedBeerRating rating = new MappedBeerRating();

		JSONObject jsonObj = new JSONObject(jsonString);

		if (jsonObj.has("id")) {
			rating.setId(jsonObj.getInt("id"));
		}

		if (jsonObj.has("user")) {
			JSONObject userJSONObj = jsonObj.getJSONObject("user");
			rating.setUser(MappedUser.createMappedUser(userJSONObj));
		}

		if (jsonObj.has("beer")) {
			JSONObject beerJSONObj = jsonObj.getJSONObject("beer");
			rating.setBeer(MappedBeer.createMappedBeer(beerJSONObj.toString()));
		}

		if (jsonObj.has("comment")) {
			rating.setComment(jsonObj.getString("comment"));
		}
		
		if (jsonObj.has("rating")) {
			rating.setRating(MappedValue.createMappedValue(jsonObj.getJSONObject("rating")));
		}

		return rating;
	}
	
	
	public static MappedBeerRating createMappedBeerRating(ResultSet rs) throws SQLException {
		int col = 1;
		MappedBeerRating rating = new MappedBeerRating();

		MappedBeer beer = rating.getBeer();
		beer.setId(rs.getInt(col++));
		beer.setName(rs.getString(col++));
		beer.setAbv(rs.getFloat(col++));
		beer.setDescript(rs.getString(col++));

		MappedUser user = rating.getUser();
		user.setId(rs.getInt(col++));
		user.setUsername(rs.getString(col++));
		user.setActive(rs.getInt(col++) == 1);

		MappedValue val = rating.getRating();
		val.setId(rs.getInt(col++));
		rating.setComment(rs.getString(col++));
		val.setDesc(rs.getString(col++));
		val.setValue(rs.getInt(col++));

		return rating;
	}

}
