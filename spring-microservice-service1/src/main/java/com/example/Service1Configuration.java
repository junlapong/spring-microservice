package com.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(HazelcastConfiguration.class)
public class Service1Configuration {
}
