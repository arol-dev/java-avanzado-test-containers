package com.example.stocks.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockCacheService {
    private final StringRedisTemplate redis;
    private static final String PREFIX = "stock:";
    private static final Duration TTL = Duration.ofMinutes(10);

    public StockCacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @SuppressWarnings("null")
    public void put(String symbol, BigDecimal price) {
        String key = key(symbol);
        redis.opsForValue().set(key, price.toPlainString(), TTL);
    }

    public Optional<BigDecimal> get(String symbol) {
        @SuppressWarnings("null")
        String v = redis.opsForValue().get(key(symbol));
        if (v == null)
            return Optional.empty();
        try {
            return Optional.of(new BigDecimal(v));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String key(String symbol) {
        return PREFIX + symbol.toUpperCase();
    }
}
