package com.example.stocks.messaging;

import java.math.BigDecimal;
import java.time.Instant;

// DTO que representa el mensaje recibido por SQS
public record StockUpdateMessage(String symbol, BigDecimal price, Instant updatedAt) {}
