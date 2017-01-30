package com.example.rest;

import com.example.hazelcast.IHazelCastUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PersonsController {

	@Autowired
	private IHazelCastUtilService hazelCastUtilService;

	final Person[] persons = {
			new Person("Tomas", "Kloucek", "Programmer"),
			new Person("Linus", "Torvalds", "Linux"),
			new Person("Heinz", "Kabutz", "Java"),
			new Person("Jonathan", "Locke", "Wicket")
	};

	@RequestMapping("/persons")
    public Persons getPersons() {
    	final Persons result = new Persons();

		System.out.println("...Getting person from controller!!...");
		for (int i=0; i < hazelCastUtilService.getBatchSize();i++) {
			result.getPersons().add(persons[i]);
		}
    	
    	return result;
    }
}
