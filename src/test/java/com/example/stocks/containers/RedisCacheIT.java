package com.example.stocks.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Prueba de integración con Redis usando Testcontainers.
 * 
 * <h2>Objetivos de aprendizaje:</h2>
 * <ul>
 * <li>Usar GenericContainer para contenedores sin soporte especializado</li>
 * <li>Entender la diferencia entre @DataRedisTest y @SpringBootTest</li>
 * <li>Probar operaciones de caché con StringRedisTemplate</li>
 * </ul>
 * 
 * <h2>Instrucciones:</h2>
 * <p>
 * Consulta el archivo <code>docs/ejercicio-redis.md</code> para ver los pasos
 * detallados.
 * </p>
 * 
 * <h2>Tareas:</h2>
 * <ol>
 * <li>Añadir las anotaciones necesarias (@Import, etc.)</li>
 * <li>Declarar el contenedor Redis con GenericContainer</li>
 * <li>Configurar @DynamicPropertySource</li>
 * <li>Inyectar StockCacheService</li>
 * <li>Implementar el test de put/get</li>
 * </ol>
 * 
 * @see <a href="docs/ejercicio-redis.md">Guía completa del ejercicio</a>
 */
@DataRedisTest
// TODO: Añadir @Import(StockCacheService.class) para importar el servicio
@Testcontainers
@ActiveProfiles("test")
class RedisCacheIT {

    // TODO: Inyectar StockCacheService

    // TODO: Declarar el contenedor Redis
    // Usar GenericContainer con imagen "redis:7-alpine"
    // Exponer puerto 6379 con withExposedPorts()
    // Usar anotación @Container

    // TODO: Añadir @DynamicPropertySource para configurar:
    // - spring.data.redis.host (usar redis::getHost)
    // - spring.data.redis.port (usar redis.getMappedPort(6379))

    @Test
    void putCache() {
        // TODO: Implementar el test
        // 1) Usar cache.put("GOOG", BigDecimal.TEN) para guardar un valor
        // 2) Usar cache.get("GOOG") para recuperarlo
        // 3) Verificar con assertThat que el valor devuelto es BigDecimal.TEN
    }
}
