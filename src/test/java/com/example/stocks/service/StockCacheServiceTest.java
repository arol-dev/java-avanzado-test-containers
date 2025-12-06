package com.example.stocks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class StockCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private StockCacheService cacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService = new StockCacheService(redisTemplate);
    }

    @Test
    void put_storesValueInRedis() {
        // Given
        String symbol = "AAPL";
        BigDecimal price = new BigDecimal("195.50");

        // When
        cacheService.put(symbol, price);

        // Then
        verify(valueOperations).set(eq("stock:AAPL"), eq("195.50"), eq(Duration.ofMinutes(10)));
    }

    @Test
    void get_returnsValueWhenPresent() {
        // Given
        String symbol = "MSFT";
        when(valueOperations.get("stock:MSFT")).thenReturn("400.00");

        // When
        Optional<BigDecimal> result = cacheService.get(symbol);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    void get_returnsEmptyWhenNotPresent() {
        // Given
        String symbol = "UNKNOWN";
        when(valueOperations.get("stock:UNKNOWN")).thenReturn(null);

        // When
        Optional<BigDecimal> result = cacheService.get(symbol);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void get_returnsEmptyWhenInvalidNumber() {
        // Given
        String symbol = "INVALID";
        when(valueOperations.get("stock:INVALID")).thenReturn("not-a-number");

        // When
        Optional<BigDecimal> result = cacheService.get(symbol);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void put_convertsSymbolToUpperCase() {
        // Given
        String symbol = "aapl";
        BigDecimal price = new BigDecimal("195.50");

        // When
        cacheService.put(symbol, price);

        // Then
        verify(valueOperations).set(eq("stock:AAPL"), anyString(), eq(Duration.ofMinutes(10)));
    }
}
