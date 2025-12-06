package com.example.stocks.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

/**
 * Esqueleto para probar el listener SQS con LocalStack.
 *
 * Qué hacer:
 * - Usa LocalStackContainer con el servicio SQS.
 * - Propaga endpoint, región y credenciales falsas a Spring (DynamicPropertySource).
 * - Crea una cola SQS (usando AWS SDK v2 o el cliente de Spring Cloud AWS) con el nombre en app.sqs.queue-name.
 * - Envía un mensaje JSON con la forma de StockUpdateMessage.
 * - Espera (Awaitility) a que el listener procese el mensaje y verifica efectos: registro en DB y valor en Redis.
 */
@SpringBootTest
@Testcontainers
public class SqsListenerIT extends BaseContainersTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
            .withServices(SQS);

    @DynamicPropertySource
    static void awsProps(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.region.static", localstack::getRegion);
        registry.add("cloud.aws.sqs.endpoint", () -> localstack.getEndpointOverride(SQS).toString());
        // credenciales ficticias
        registry.add("cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localstack::getSecretKey);
    }

    @Test
    void ejemplo_listener_sqs() {
        // TODO:
        // 1) Crear la cola si no existe usando el SDK de AWS (SqsClient) apuntando al endpoint de LocalStack.
        // 2) Enviar un mensaje con el JSON de StockUpdateMessage, por ejemplo:
        //    {"symbol":"AAPL","price":195.12,"updatedAt":"2025-01-01T12:00:00Z"}
        // 3) Esperar hasta que la aplicación procese el mensaje (usar Awaitility o similar)
        // 4) Verificar que el Stock esté en la base de datos y que el precio esté en Redis.
    }
}
