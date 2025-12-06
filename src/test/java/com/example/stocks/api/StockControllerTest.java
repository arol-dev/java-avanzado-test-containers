package com.example.stocks.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.stocks.domain.Stock;
import com.example.stocks.service.StockService;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockService stockService;

    private StockController controller;

    @BeforeEach
    void setUp() {
        controller = new StockController(stockService);
    }

    @Test
    void list_returnsAllStocks() {
        // Given
        List<Stock> stocks = List.of(
                new Stock("AAPL", new BigDecimal("195.00"), Instant.now()),
                new Stock("MSFT", new BigDecimal("400.00"), Instant.now()));
        when(stockService.findAll()).thenReturn(stocks);

        // When
        List<Stock> result = controller.list();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void get_returnsStockView_whenFound() {
        // Given
        String symbol = "AAPL";
        StockService.StockView view = new StockService.StockView(
                symbol, new BigDecimal("195.00"), Instant.now(), false);
        when(stockService.getView(symbol)).thenReturn(Optional.of(view));

        // When
        ResponseEntity<StockService.StockView> response = controller.get(symbol);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().symbol()).isEqualTo(symbol);
    }

    @Test
    void get_returnsNotFound_whenStockNotExists() {
        // Given
        String symbol = "UNKNOWN";
        when(stockService.getView(symbol)).thenReturn(Optional.empty());

        // When
        ResponseEntity<StockService.StockView> response = controller.get(symbol);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
