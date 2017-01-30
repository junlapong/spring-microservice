package com.example.hazelcast;

import org.springframework.cache.annotation.Cacheable;

/**
 * Created by tomas.kloucek on 18.1.2017.
 */
public interface IHazelCastUtilService {
    @Cacheable("batchSize")
    int getBatchSize();
}
