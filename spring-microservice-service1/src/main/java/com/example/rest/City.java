package com.example.rest;

import java.io.Serializable;

public class City implements Serializable{
	private String name;
	private String state;

	public City(final String name, final String state) {
		this.name = name;
		this.state = state;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
