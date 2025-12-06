package com.example.stocks.service;

import com.example.stocks.domain.Stock;
import com.example.stocks.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    private final StockRepository repository;
    private final StockCacheService cacheService;

    public StockService(StockRepository repository, StockCacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    public List<Stock> findAll() {
        return repository.findAll();
    }

    public Optional<StockView> getView(String symbol) {
        String s = symbol.toUpperCase();
        Optional<BigDecimal> cached = cacheService.get(s);
        return cached
                .map(bigDecimal -> repository.findBySymbol(s)
                        .map(st -> new StockView(st.getSymbol(), bigDecimal, st.getUpdatedAt(), true)))
                .orElseGet(() -> repository.findBySymbol(s)
                        .map(st -> new StockView(st.getSymbol(), st.getPrice(), st.getUpdatedAt(), false)));
    }

    @Transactional
    public Stock upsertFromMessage(String symbol, BigDecimal price, Instant updatedAt) {
        String s = symbol.toUpperCase();
        Stock stock = repository.findBySymbol(s)
                .map(existing -> {
                    existing.setPrice(price);
                    existing.setUpdatedAt(updatedAt);
                    return existing;
                })
                .orElseGet(() -> new Stock(s, price, updatedAt));
        Stock saved = repository.save(stock);
        cacheService.put(s, price);
        return saved;
    }

    public record StockView(String symbol, BigDecimal price, Instant updatedAt,
                            boolean fromCache) {
    }
}
