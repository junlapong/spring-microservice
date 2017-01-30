package com.example.rest;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Persons implements Serializable{
	protected List<Person> persons = new ArrayList<Person>();

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
}
