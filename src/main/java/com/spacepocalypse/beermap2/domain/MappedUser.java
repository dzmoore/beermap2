package com.spacepocalypse.beermap2.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;

public class MappedUser implements Serializable {
	private static final long serialVersionUID = -2121990998905286418L;
	private int id;
	private String username;
	private boolean active;
	
	public MappedUser() {
		setUsername("");
		setActive(false);
		setId(-1);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public static MappedUser createMappedUser(ResultSet rs) throws SQLException {
		int col = 1;
		MappedUser user = new MappedUser();

		user.setId(rs.getInt(col++));
		user.setUsername(rs.getString(col++));
		user.setActive(rs.getInt(col++) == 1);
		return user;
	}

	public static MappedUser createMappedUser(JSONObject jsonObj) throws JSONException {
		MappedUser user = new MappedUser();

		if (jsonObj.has("id")) {
			user.setId(jsonObj.getInt("id"));
		}

		if (jsonObj.has("username")) {
			user.setUsername(jsonObj.getString("username"));
		}

		if (jsonObj.has("active")) {
			user.setActive(jsonObj.getBoolean("active"));
		}
		return user;
	}
	
	public static void main(String[] args) {
		String jsonObject = new JSONObject(new MappedUser()).toString();
		System.out.println(jsonObject);
	}
}
