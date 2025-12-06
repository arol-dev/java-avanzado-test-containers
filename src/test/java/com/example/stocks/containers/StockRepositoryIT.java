package com.example.stocks.containers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test for StockRepository using Testcontainers.
 * Provisions PostgreSQL, LocalStack (SQS), and Redis containers.
 */
@SpringBootTest
@Testcontainers
public class StockRepositoryIT extends BaseContainersTest {

    @SuppressWarnings("resource")
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.4"))
            .withServices(SQS);

    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @SuppressWarnings("resource")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        localstack.start();
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // LocalStack / SQS
        registry.add("spring.cloud.aws.region.static", localstack::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localstack.getEndpointOverride(SQS).toString());
        registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.stocks.repository.StockRepository stockRepository;

    @Test
    void ejemplo_prueba_persistencia() {
        // 1) crear y guardar un Stock
        String symbol = "NVDA";
        java.math.BigDecimal price = new java.math.BigDecimal("450.00");
        com.example.stocks.domain.Stock stock = new com.example.stocks.domain.Stock(symbol, price,
                java.time.Instant.now());
        stockRepository.save(stock);

        // 2) recuperar por símbolo y hacer aserciones
        java.util.Optional<com.example.stocks.domain.Stock> found = stockRepository.findBySymbol(symbol);
        assertThat(found).isPresent();
        assertThat(found.get().getSymbol()).isEqualTo(symbol);
        assertThat(found.get().getPrice()).isEqualByComparingTo(price);
        assertThat(found.get().getId()).isNotNull();
    }
}
