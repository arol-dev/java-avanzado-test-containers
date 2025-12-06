package com.example.stocks.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Esqueleto de prueba de integración con Postgres usando Testcontainers.
 *
 * Qué hacer:
 * - Usa el contenedor de Postgres provisto abajo.
 * - Expón sus propiedades a Spring usando @DynamicPropertySource.
 * - Escribe pruebas sobre el repositorio JPA (p. ej., guardar y leer un Stock).
 */
@SpringBootTest
@Testcontainers
public class StockRepositoryIT extends BaseContainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void dbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void ejemplo_prueba_persistencia() {
        // TODO: Inyecta StockRepository y realiza una operación simple:
        // 1) crear y guardar un Stock
        // 2) recuperar por símbolo y hacer aserciones
    }
}
