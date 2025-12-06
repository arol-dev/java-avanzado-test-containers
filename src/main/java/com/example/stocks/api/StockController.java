package com.example.stocks.api;

import com.example.stocks.domain.Stock;
import com.example.stocks.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @GetMapping
    public List<Stock> list() {
        return service.findAll();
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<StockService.StockView> get(@PathVariable String symbol) {
        return service.getView(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
