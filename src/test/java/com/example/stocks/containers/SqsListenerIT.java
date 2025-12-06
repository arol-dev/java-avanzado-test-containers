package com.example.stocks.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Prueba de integración con LocalStack (SQS) usando Testcontainers.
 * 
 * <h2>Objetivos de aprendizaje:</h2>
 * <ul>
 * <li>Configurar LocalStackContainer para simular servicios AWS</li>
 * <li>Usar AWS SDK v2 para interactuar con SQS</li>
 * <li>Verificar procesamiento asíncrono con Awaitility</li>
 * <li>Probar flujos end-to-end: mensaje → listener → DB + Redis</li>
 * </ul>
 * 
 * <h2>Instrucciones:</h2>
 * <p>
 * Consulta el archivo <code>docs/ejercicio-sqs.md</code> para ver los pasos
 * detallados.
 * </p>
 * 
 * <h2>Tareas:</h2>
 * <ol>
 * <li>Declarar los contenedores (LocalStack, PostgreSQL, Redis)</li>
 * <li>Configurar @DynamicPropertySource con propiedades AWS y DB</li>
 * <li>Inyectar StockRepository, StockCacheService y el nombre de la cola</li>
 * <li>Crear la cola SQS usando SqsClient</li>
 * <li>Enviar un mensaje JSON con el formato de StockUpdateMessage</li>
 * <li>Usar Awaitility para esperar el procesamiento</li>
 * <li>Verificar datos en DB y Redis</li>
 * </ol>
 * 
 * @see <a href="docs/ejercicio-sqs.md">Guía completa del ejercicio</a>
 */
@SpringBootTest
@Testcontainers
public class SqsListenerIT extends BaseContainersTest {

        // TODO: Declarar los contenedores
        // - LocalStackContainer con .withServices(SQS)
        // - PostgreSQLContainer
        // - GenericContainer para Redis

        // TODO: Añadir bloque static {} para iniciar los contenedores

        // TODO: Añadir @DynamicPropertySource para configurar:
        // - spring.cloud.aws.region.static
        // - spring.cloud.aws.sqs.endpoint
        // - spring.cloud.aws.credentials.access-key
        // - spring.cloud.aws.credentials.secret-key
        // - spring.datasource.* (url, username, password)
        // - spring.data.redis.* (host, port)

        // TODO: Inyectar StockRepository
        // TODO: Inyectar StockCacheService
        // TODO: Inyectar el nombre de la cola (@Value("${app.sqs.queue-name}"))

        @Test
        void ejemplo_listener_sqs() {
                // TODO: Implementar el test siguiendo estos pasos:

                // 1) Crear un SqsClient conectado a LocalStack
                // - Usar SqsClient.builder()
                // - Configurar endpointOverride, region y credentialsProvider

                // 2) Crear la cola SQS
                // - sqsClient.createQueue(r -> r.queueName(queueName))
                // - Obtener la URL de la cola

                // 3) Construir y enviar el mensaje JSON
                // - Formato:
                // {"symbol":"AAPL","price":195.12,"updatedAt":"2025-01-01T12:00:00Z"}
                // - Usar sqsClient.sendMessage()

                // 4) Esperar el procesamiento con Awaitility
                // - Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(...)

                // 5) Dentro de untilAsserted, verificar:
                // - Que el Stock existe en la base de datos
                // - Que el precio coincide
                // - Que el precio está en la caché de Redis
        }
}
