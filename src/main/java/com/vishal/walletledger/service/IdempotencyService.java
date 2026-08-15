package com.vishal.walletledger.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("idempotency:" + key)
        );
    }

    public void save(String key) {
        redisTemplate.opsForValue().set(
                "idempotency:" + key,
                "processed",
                Duration.ofHours(24)
        );
    }
}