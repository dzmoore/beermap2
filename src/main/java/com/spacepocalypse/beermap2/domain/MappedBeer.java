package com.spacepocalypse.beermap2.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.spacepocalypse.beermap2.domain.json.JSONArray;
import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;

public class MappedBeer implements Serializable {
	private static final long serialVersionUID = 2563267495110624938L;

	private int id;
	private String name;
	private float abv;
	private String descript;
	private String upc;
	
	public MappedBeer() {
		setName("");
		setAbv(-1);
		setDescript("");
		setUpc("");
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setAbv(float abv) {
		this.abv = abv;
	}
	public float getAbv() {
		return abv;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getDescript() {
		return descript;
	}
	
	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getUpc() {
		return upc;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=[");
		sb.append(getName());
		sb.append("] abv=[");
		sb.append(getAbv());
		sb.append("] descript=[");
		sb.append(getDescript());
		sb.append("] upc=[");
		sb.append(getUpc());
		sb.append("]");
		return sb.toString();

	}
	
	public static List<MappedBeer> createListFromJSONArray(String response) throws JSONException, NumberFormatException {
		JSONArray resultsArr = null;
		try {
			resultsArr = new JSONArray(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<MappedBeer> searchResults = new ArrayList<MappedBeer>();

		for (int i = 0; i < resultsArr.length(); i++) {
			JSONObject ea = resultsArr.getJSONObject(i);
			MappedBeer eaMappedValue = new MappedBeer();
			if (ea.has("id")) {
				eaMappedValue.setId(ea.getInt("id"));
			}

			if (ea.has("name")) {
				eaMappedValue.setName(ea.getString("name"));
			}

			if (ea.has("abv")) {
				eaMappedValue.setAbv(Double.valueOf(ea.getDouble("abv")).floatValue());
			}

			if (ea.has("descript")) {
				eaMappedValue.setDescript(ea.getString("descript"));
			}

			searchResults.add(eaMappedValue);
		}
		return searchResults;
	}

	public static MappedBeer createMappedBeer(String json) throws JSONException, NumberFormatException {
		JSONObject ea = new JSONObject(json);
		MappedBeer eaMappedValue = new MappedBeer();
		if (ea.has("id")) {
			eaMappedValue.setId(ea.getInt("id"));
		}

		if (ea.has("name")) {
			eaMappedValue.setName(ea.getString("name"));
		}

		if (ea.has("abv")) {
			eaMappedValue.setAbv(Double.valueOf(ea.getDouble("abv")).floatValue());
		}

		if (ea.has("descript")) {
			eaMappedValue.setDescript(ea.getString("descript"));
		}

		return eaMappedValue;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	
}
