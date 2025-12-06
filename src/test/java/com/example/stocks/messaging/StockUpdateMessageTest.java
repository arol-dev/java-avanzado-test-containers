package com.example.stocks.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class StockUpdateMessageTest {

    @Test
    void record_storesAllFields() {
        // Given
        String symbol = "AAPL";
        BigDecimal price = new BigDecimal("195.00");
        Instant now = Instant.now();

        // When
        StockUpdateMessage message = new StockUpdateMessage(symbol, price, now);

        // Then
        assertThat(message.symbol()).isEqualTo(symbol);
        assertThat(message.price()).isEqualByComparingTo(price);
        assertThat(message.updatedAt()).isEqualTo(now);
    }

    @Test
    void record_supportsNullUpdatedAt() {
        // When
        StockUpdateMessage message = new StockUpdateMessage("MSFT", new BigDecimal("400.00"), null);

        // Then
        assertThat(message.updatedAt()).isNull();
    }
}
