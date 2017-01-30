package com.example.rest;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Cities implements Serializable{
	protected List<City> cities = new ArrayList<City>();

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}
}
