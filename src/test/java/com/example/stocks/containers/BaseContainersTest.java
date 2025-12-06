package com.example.stocks.containers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.ActiveProfiles;

/**
 * Esqueleto base para pruebas de integración con Testcontainers.
 *
 * Instrucciones:
 * - Extiende esta clase desde tus tests @SpringBootTest para reutilizar la configuración común.
 * - Añade contenedores estáticos por clase y propágalos a Spring mediante @DynamicPropertySource.
 * - Arranca solo los servicios que necesites: Postgres, Redis (GenericContainer) y LocalStack (SQS).
 */
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseContainersTest {

    @BeforeAll
    void beforeAll() {
        // Opcional: lógica común antes de todos los tests.
    }
}
