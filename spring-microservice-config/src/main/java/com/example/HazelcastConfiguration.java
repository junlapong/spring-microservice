package com.example;

import com.example.hazelcast.HazelCastUtilService;
import com.example.hazelcast.IHazelCastUtilService;
import com.hazelcast.config.Config;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

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

    @Bean
    IHazelCastUtilService hazelCastUtilService() {
        return new HazelCastUtilService();
    }

    @Bean
    HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(config());
    }

    @Bean
    public CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelcastInstance());
    }
}