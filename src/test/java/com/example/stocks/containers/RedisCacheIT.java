package com.example.stocks.containers;

import com.example.stocks.service.StockCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ejemplo de configuración de prueba mínima para Spring Boot con Testcontainers
 * y Redis.
 *
 * Elementos clave para esta configuración mínima:
 * </p>
 * - @DataRedisTest: Esta anotación carga solo los componentes de Spring Data
 * Redis, evitando cargar el contexto completo de la aplicación.
 * - @Testcontainers: Activa el soporte de Testcontainers para JUnit 5,
 * gestionando el ciclo de vida de los contenedores.
 * - @Container: Marca la instancia de RedisContainer para que sea gestionada
 * por Testcontainers.
 * - @DynamicPropertySource: Registra dinámicamente el host y el puerto del
 * contenedor Redis para la autoconfiguración de Spring Boot.
 * - @Import(StockCacheService.class): Importa solo el servicio necesario para
 * esta prueba.
 */
@DataRedisTest
@Import(StockCacheService.class)
@Testcontainers
@ActiveProfiles("test")
class RedisCacheIT {

    @Autowired
    private StockCacheService cache;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void putCache() {
        cache.put("GOOG", BigDecimal.TEN);
        assertThat(cache.get("GOOG")).hasValue(BigDecimal.TEN);
    }
}
