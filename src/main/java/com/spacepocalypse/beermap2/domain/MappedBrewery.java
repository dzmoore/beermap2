package com.spacepocalypse.beermap2.domain;

import java.io.Serializable;

import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;
import com.spacepocalypse.beermap2.service.Constants;

public class MappedBrewery implements Serializable{
	private static final long serialVersionUID = -2139853777020090397L;
	
	private int id;
	private String name;
	private String country;
	private String descript;
	
	public MappedBrewery() {
		id = Constants.INVALID_ID;
		name = "";
		country = "";
		descript = "";
	}
	
	public static MappedBrewery createMappedBrewery(JSONObject obj) {
		final MappedBrewery ret = new MappedBrewery();
		if (obj.has("id")) {
			try {
				ret.setId(obj.getInt("id"));
			} catch (JSONException e) {}
		}
		
		if (obj.has("name")) {
			try {
				ret.setName(obj.getString("name"));
			} catch (JSONException e) {}
		}
		
		if (obj.has("country")) {
			try {
				ret.setCountry(obj.getString("country"));
			} catch (JSONException e) {}
		}
		
		if (obj.has("descript")) {
			try {
				ret.setDescript(obj.getString("descript"));
			} catch (JSONException e) {}
		}
		
		return ret;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappedBrewery [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", country=");
		builder.append(country);
		builder.append(", descript=");
		builder.append(descript);
		builder.append("]");
		return builder.toString();
	}
	
	
}
