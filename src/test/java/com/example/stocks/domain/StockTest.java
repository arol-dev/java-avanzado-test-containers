package com.example.stocks.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class StockTest {

    @Test
    void constructor_setsAllFields() {
        // Given
        String symbol = "AAPL";
        BigDecimal price = new BigDecimal("195.00");
        Instant now = Instant.now();

        // When
        Stock stock = new Stock(symbol, price, now);

        // Then
        assertThat(stock.getSymbol()).isEqualTo(symbol);
        assertThat(stock.getPrice()).isEqualByComparingTo(price);
        assertThat(stock.getUpdatedAt()).isEqualTo(now);
        assertThat(stock.getId()).isNull(); // ID not set until persisted
    }

    @Test
    void defaultConstructor_createsEmptyStock() {
        // When
        Stock stock = new Stock();

        // Then
        assertThat(stock.getSymbol()).isNull();
        assertThat(stock.getPrice()).isNull();
        assertThat(stock.getUpdatedAt()).isNull();
    }

    @Test
    void setters_updateFields() {
        // Given
        Stock stock = new Stock();
        String symbol = "MSFT";
        BigDecimal price = new BigDecimal("400.00");
        Instant now = Instant.now();

        // When
        stock.setSymbol(symbol);
        stock.setPrice(price);
        stock.setUpdatedAt(now);

        // Then
        assertThat(stock.getSymbol()).isEqualTo(symbol);
        assertThat(stock.getPrice()).isEqualByComparingTo(price);
        assertThat(stock.getUpdatedAt()).isEqualTo(now);
    }
}
