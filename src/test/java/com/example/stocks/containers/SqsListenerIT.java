package com.example.stocks.containers;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Esqueleto para probar el listener SQS con LocalStack.
 *
 * Qué hacer:
 * - Usa LocalStackContainer con el servicio SQS.
 * - Propaga endpoint, región y credenciales falsas a Spring
 * (DynamicPropertySource).
 * - Crea una cola SQS (usando AWS SDK v2 o el cliente de Spring Cloud AWS) con
 * el nombre en app.sqs.queue-name.
 * - Envía un mensaje JSON con la forma de StockUpdateMessage.
 * - Espera (Awaitility) a que el listener procese el mensaje y verifica
 * efectos: registro en DB y valor en Redis.
 */
@SpringBootTest
@Testcontainers
public class SqsListenerIT extends BaseContainersTest {

    @SuppressWarnings("resource")
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
            .withServices(SQS);

    @SuppressWarnings("resource")
    static org.testcontainers.containers.PostgreSQLContainer<?> postgres = new org.testcontainers.containers.PostgreSQLContainer<>(
            "postgres:16-alpine");

    @SuppressWarnings("resource")
    static org.testcontainers.containers.GenericContainer<?> redis = new org.testcontainers.containers.GenericContainer<>(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        localstack.start();
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void awsProps(DynamicPropertyRegistry registry) {
        // AWS / LocalStack
        registry.add("spring.cloud.aws.region.static", localstack::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localstack.getEndpointOverride(SQS).toString());
        registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);

        // Postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.stocks.repository.StockRepository stockRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.stocks.service.StockCacheService stockCacheService;

    @org.springframework.beans.factory.annotation.Value("${app.sqs.queue-name}")
    private String queueName;

    @Test
    void ejemplo_listener_sqs() {
        // 1) Crear la cola si no existe usando el SDK de AWS (SqsClient)
        try (software.amazon.awssdk.services.sqs.SqsClient sqsClient = software.amazon.awssdk.services.sqs.SqsClient
                .builder()
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .region(software.amazon.awssdk.regions.Region.of(localstack.getRegion()))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                        software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                                localstack.getAccessKey(), localstack.getSecretKey())))
                .build()) {

            sqsClient.createQueue(r -> r.queueName(queueName));
            String queueUrl = sqsClient.getQueueUrl(r -> r.queueName(queueName)).queueUrl();

            // 2) Enviar un mensaje con el JSON de StockUpdateMessage
            String symbol = "AAPL";
            java.math.BigDecimal price = new java.math.BigDecimal("195.12");
            String jsonBody = """
                    {
                      "symbol": "%s",
                      "price": %s,
                      "updatedAt": "%s"
                    }
                    """.formatted(symbol, price, java.time.Instant.now());

            sqsClient.sendMessage(r -> r.queueUrl(queueUrl).messageBody(jsonBody));

            // 3) Esperar hasta que la aplicación procese el mensaje
            org.awaitility.Awaitility.await()
                    .atMost(java.time.Duration.ofSeconds(10))
                    .untilAsserted(() -> {
                        // 4) Verificar DB y Redis
                        java.util.Optional<com.example.stocks.domain.Stock> stock = stockRepository
                                .findBySymbol(symbol);
                        org.assertj.core.api.Assertions.assertThat(stock).isPresent();
                        org.assertj.core.api.Assertions.assertThat(stock.get().getPrice()).isEqualByComparingTo(price);

                        java.util.Optional<java.math.BigDecimal> cachedPrice = stockCacheService.get(symbol);
                        org.assertj.core.api.Assertions.assertThat(cachedPrice).isPresent();
                        org.assertj.core.api.Assertions.assertThat(cachedPrice.get()).isEqualByComparingTo(price);
                    });
        }
    }
}
