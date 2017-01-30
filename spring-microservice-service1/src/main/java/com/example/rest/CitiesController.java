package com.example.rest;

import com.example.hazelcast.IHazelCastUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CitiesController {
	final City[] cities = {
			new City("Brno", "Czech republic"),
			new City("Bern", "Switzeland"),
			new City("Berlin", "Germany"),
			new City("London", "England")
	};

	@Autowired
	private IHazelCastUtilService hazelCastUtilService;

	@RequestMapping("/cities")
    public Cities getCities() {
    	final Cities result = new Cities();

		System.out.println("...Getting city from controller!!...");
		for (int i=0; i < hazelCastUtilService.getBatchSize();i++) {
			result.getCities().add(cities[i]);
		}

    	return result;
    }
}
