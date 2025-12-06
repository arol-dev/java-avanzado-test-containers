package com.example.stocks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

import com.example.stocks.domain.Stock;
import com.example.stocks.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository repository;

    @Mock
    private StockCacheService cacheService;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(repository, cacheService);
    }

    @Test
    void findAll_returnsAllStocks() {
        // Given
        List<Stock> expected = List.of(
                new Stock("AAPL", new BigDecimal("195.00"), Instant.now()),
                new Stock("MSFT", new BigDecimal("400.00"), Instant.now()));
        when(repository.findAll()).thenReturn(expected);

        // When
        List<Stock> result = stockService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }

    @Test
    void getView_whenCached_returnsViewWithCachedPrice() {
        // Given
        String symbol = "AAPL";
        BigDecimal cachedPrice = new BigDecimal("200.00");
        Stock stock = new Stock(symbol, new BigDecimal("195.00"), Instant.now());

        when(cacheService.get(symbol.toUpperCase())).thenReturn(Optional.of(cachedPrice));
        when(repository.findBySymbol(symbol.toUpperCase())).thenReturn(Optional.of(stock));

        // When
        Optional<StockService.StockView> result = stockService.getView(symbol);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().fromCache()).isTrue();
        assertThat(result.get().price()).isEqualByComparingTo(cachedPrice);
    }

    @Test
    void getView_whenNotCached_returnsViewFromDatabase() {
        // Given
        String symbol = "MSFT";
        BigDecimal dbPrice = new BigDecimal("400.00");
        Stock stock = new Stock(symbol, dbPrice, Instant.now());

        when(cacheService.get(symbol.toUpperCase())).thenReturn(Optional.empty());
        when(repository.findBySymbol(symbol.toUpperCase())).thenReturn(Optional.of(stock));

        // When
        Optional<StockService.StockView> result = stockService.getView(symbol);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().fromCache()).isFalse();
        assertThat(result.get().price()).isEqualByComparingTo(dbPrice);
    }

    @Test
    void getView_whenNotFound_returnsEmpty() {
        // Given
        String symbol = "UNKNOWN";

        when(cacheService.get(symbol.toUpperCase())).thenReturn(Optional.empty());
        when(repository.findBySymbol(symbol.toUpperCase())).thenReturn(Optional.empty());

        // When
        Optional<StockService.StockView> result = stockService.getView(symbol);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void upsertFromMessage_createsNewStock_whenNotExists() {
        // Given
        String symbol = "NVDA";
        BigDecimal price = new BigDecimal("450.00");
        Instant now = Instant.now();

        when(repository.findBySymbol(symbol.toUpperCase())).thenReturn(Optional.empty());
        when(repository.save(any(Stock.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Stock result = stockService.upsertFromMessage(symbol, price, now);

        // Then
        assertThat(result.getSymbol()).isEqualTo(symbol.toUpperCase());
        assertThat(result.getPrice()).isEqualByComparingTo(price);
        verify(repository).save(any(Stock.class));
        verify(cacheService).put(symbol.toUpperCase(), price);
    }

    @Test
    void upsertFromMessage_updatesExistingStock() {
        // Given
        String symbol = "AAPL";
        BigDecimal oldPrice = new BigDecimal("195.00");
        BigDecimal newPrice = new BigDecimal("200.00");
        Instant now = Instant.now();
        Stock existing = new Stock(symbol, oldPrice, Instant.now());

        when(repository.findBySymbol(symbol.toUpperCase())).thenReturn(Optional.of(existing));
        when(repository.save(any(Stock.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Stock result = stockService.upsertFromMessage(symbol, newPrice, now);

        // Then
        assertThat(result.getPrice()).isEqualByComparingTo(newPrice);
        assertThat(result.getUpdatedAt()).isEqualTo(now);
        verify(cacheService).put(symbol.toUpperCase(), newPrice);
    }
}
