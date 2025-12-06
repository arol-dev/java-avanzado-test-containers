package com.example.stocks.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.stocks.service.StockService;

@ExtendWith(MockitoExtension.class)
class SqsStockListenerTest {

    @Mock
    private StockService stockService;

    private SqsStockListener listener;

    @BeforeEach
    void setUp() {
        listener = new SqsStockListener(stockService);
    }

    @Test
    void onMessage_processesValidMessage() {
        // Given
        Instant now = Instant.now();
        StockUpdateMessage message = new StockUpdateMessage("AAPL", new BigDecimal("195.00"), now);

        // When
        listener.onMessage(message);

        // Then
        verify(stockService).upsertFromMessage(eq("AAPL"), eq(new BigDecimal("195.00")), eq(now));
    }

    @Test
    void onMessage_usesCurrentTime_whenUpdatedAtIsNull() {
        // Given
        StockUpdateMessage message = new StockUpdateMessage("MSFT", new BigDecimal("400.00"), null);

        // When
        listener.onMessage(message);

        // Then
        verify(stockService).upsertFromMessage(eq("MSFT"), eq(new BigDecimal("400.00")), any(Instant.class));
    }

    @Test
    void onMessage_doesNothing_whenMessageIsNull() {
        // When
        listener.onMessage(null);

        // Then
        verify(stockService, never()).upsertFromMessage(any(), any(), any());
    }
}
