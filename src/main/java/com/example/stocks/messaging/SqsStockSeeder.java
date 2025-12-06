package com.example.stocks.messaging;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Simple seeder that publishes a few stock updates to the SQS queue at startup.
 */
@Component
public class SqsStockSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SqsStockSeeder.class);

    private final SqsTemplate sqsTemplate;
    private final String queueName;

    public SqsStockSeeder(SqsTemplate sqsTemplate,
            @Value("${app.sqs.queue-name}") String queueName) {
        this.sqsTemplate = sqsTemplate;
        this.queueName = Objects.requireNonNull(queueName, "queueName must not be null");
    }

    @Override
    @SuppressWarnings("null")
    public void run(String... args) {
        // Prepare at least 5 sample stock updates
        List<StockUpdateMessage> messages = List.of(
                new StockUpdateMessage("AAPL", new BigDecimal("195.12"), Instant.now()),
                new StockUpdateMessage("MSFT", new BigDecimal("421.87"), Instant.now()),
                new StockUpdateMessage("AMZN", new BigDecimal("176.45"), Instant.now()),
                new StockUpdateMessage("GOOGL", new BigDecimal("147.33"), Instant.now()),
                new StockUpdateMessage("TSLA", new BigDecimal("234.56"), Instant.now()));

        messages.forEach(msg -> {
            sqsTemplate.send(to -> to.queue(queueName).payload(msg));
            log.info("Seed message sent to SQS: {} {} @ {}", msg.symbol(), msg.price(), msg.updatedAt());
        });
    }
}
