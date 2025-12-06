package com.example.stocks.messaging;

import com.example.stocks.service.StockService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SqsStockListener {
    private static final Logger log = LoggerFactory.getLogger(SqsStockListener.class);
    private final StockService stockService;

    public SqsStockListener(StockService stockService) {
        this.stockService = stockService;
    }

    // Nota: el nombre de la cola se externaliza en application.yml
    @SqsListener("${app.sqs.queue-name}")
    public void onMessage(@Payload StockUpdateMessage msg) {
        if (msg == null) return;
        log.info("Mensaje SQS recibido: {} {} @ {}", msg.symbol(), msg.price(), msg.updatedAt());
        Instant ts = msg.updatedAt() != null ? msg.updatedAt() : Instant.now();
        stockService.upsertFromMessage(msg.symbol(), msg.price(), ts);
    }
}
