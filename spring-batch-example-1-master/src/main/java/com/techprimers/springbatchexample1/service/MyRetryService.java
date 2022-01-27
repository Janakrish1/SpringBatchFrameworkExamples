package com.techprimers.springbatchexample1.service;

import org.springframework.stereotype.Service;

import java.util.Random;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Service
public class MyRetryService {
    
    @Retryable(maxAttempts = 10, include = RuntimeException.class, backoff = @Backoff(delay = 100, multiplier = 2))
    public boolean process() {
        
    	Random randNum = new Random();
    	int num = randNum.nextInt();
        
        if (num % 2 != 0) {
            throw new RuntimeException("Random fail time!");
        }
        
        return true;
    }

}