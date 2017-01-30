# Writing MicroServices [part 10] #

## Caching with HazelCast ##
folk of: https://bitbucket.org/tomask79/microservice-spring-hazelcast-caching 

In [part 7](https://bitbucket.org/tomask79/microservice-spring-cloud-bus) I showed howto use publish configuration settings from Spring Cloud Config server to MicroServices nodes via RabbitMQ. Cons of this solution:

* Spring Cloud Config supports only GIT repo for saving configurations
* You need to manually send event to all MicroServices nodes when data at config server change

Lets consider another solution, how about to remove Spring Config Server and replace the distribution of configuration data to nodes with [HazelCast](https://hazelcast.com/) datagrid.

## Configuration of Hazelcast at MicroServices nodes ##

First you need to add maven dependency for Hazelcast:
(hazelcast-spring, because we're going to be using Spring Caching API with Hazelcast as CACHE provider)
```xml
<!-- https://mvnrepository.com/artifact/com.hazelcast/hazelcast-spring -->
<dependency>
	<groupId>com.hazelcast</groupId>
	<artifactId>hazelcast-spring</artifactId>
</dependency>
```
Then you need to configure Hazelcast, you've got following options:

* Configure com.hazelcast.config.Config
  **or**
* Put hazelcast.xml configuration to classpath. See this [documentation](http://docs.hazelcast.org/docs/2.2/manual/html/ch12s06.html)
  **or** 
* If you're not fine with hazelcast.xml name, set -Dhazelcast.config variable with your file

My configuration looks:


```java
@Bean
public Config config() {
    Config config = new Config();

    config.setInstanceName("HazelcastService");
    config.setProperty("hazelcast.wait.seconds.before.join","10");

    config.getGroupConfig().setName("mygroup");
    config.getGroupConfig().setPassword("mypassword");

    config.getNetworkConfig().setPortAutoIncrement(true);
    config.getNetworkConfig().setPort(10555);
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);

    SSLConfig sslConfig = new SSLConfig();
    sslConfig.setEnabled(false);
    config.getNetworkConfig().setSSLConfig(sslConfig);

    return config;
}
```
* Always use Hazelcast groups. Do not interfere with others. Check GroupConfig.
* Always use port incrementation. Sometimes ports at some environments could be occupied. 

if you plan to use Hazelcast as distributed cache then you need override cacheManager bean with Hazelcast implementation:

```java
 @Bean
 HazelcastInstance hazelcastInstance() {
     return Hazelcast.newHazelcastInstance(config());
 }

 @Bean
 public CacheManager cacheManager() {
     return new HazelcastCacheManager(hazelcastInstance());
 }
```



## MicroServices with HazelCast caching ##

Lets have following Util service:

```java
package com.example.hazelcast;

import org.springframework.cache.annotation.Cacheable;

/**
 * Created by tomas.kloucek on 18.1.2017.
 */
public interface IHazelCastUtilService {
    @Cacheable("batchSize")
    int getBatchSize();
}
```
with implementation 

```java
public class HazelCastUtilService implements IHazelCastUtilService {
    @Override
    public int getBatchSize() {
        try {
            System.out.println("Getting batch size from DAO...");
            TimeUnit.SECONDS.sleep(5);  // (1)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 3;
    }
}
```

and lets share this service among the MicroServices in the following context:

Cities MicroService:

```java
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
```
Persons MicroService:

```java
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
```

## Testing the demo ##

```
git clone <this repo>
mvn clean install (in the root folder with pom.xml)
cd spring-microservice-registry
java -jar target/registry-0.0.1-SNAPSHOT.war
verify that NetFlix Eureka is running at http://localhost:9761

cd ..
cd spring-microservice-service1
java -jar target/service1-0.0.1-SNAPSHOT.war
verify at http://localhost:9761 that citiesService has been registered

cd ..
cd spring-microservice-service2
java -jar target/service2-0.0.1-SNAPSHOT.war
verify at http://localhost:9761 that personsService has been registered
```
to be sure that **both** MicroServices form Hazelcast cluster, you need to see something like:

```
Members [2] {
        Member [10.130.48.104]:10555 this
        Member [10.130.48.104]:10556
}
```

Now hit http://localhost:8081/cities

you should see then in the console something like:

```
...Getting city from controller!!...
Getting batch size from DAO...
```
Now hit http://localhost:8082/persons, to invoke second MicroService in the cluster:

you should see just:

```
...Getting person from controller!!...
```
Because "batchSize" settings was already cached by cities MicroService before. Pretty sweet!

regards

Tomas