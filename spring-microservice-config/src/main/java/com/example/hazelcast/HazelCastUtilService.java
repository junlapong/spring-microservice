package com.example.hazelcast;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by tomas.kloucek on 18.1.2017.
 */
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
