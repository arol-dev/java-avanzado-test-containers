package com.example.stocks.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Prueba de integración con PostgreSQL usando Testcontainers.
 * 
 * <h2>Objetivos de aprendizaje:</h2>
 * <ul>
 * <li>Configurar un contenedor PostgreSQL con Testcontainers</li>
 * <li>Usar @DynamicPropertySource para inyectar propiedades</li>
 * <li>Probar operaciones CRUD con Spring Data JPA</li>
 * </ul>
 * 
 * <h2>Instrucciones:</h2>
 * <p>
 * Consulta el archivo <code>docs/ejercicio-postgres.md</code> para ver los
 * pasos detallados.
 * </p>
 * 
 * <h2>Tareas:</h2>
 * <ol>
 * <li>Declarar los contenedores necesarios (PostgreSQL, Redis, LocalStack)</li>
 * <li>Configurar @DynamicPropertySource para las propiedades de conexión</li>
 * <li>Inyectar StockRepository</li>
 * <li>Implementar el test: crear un Stock, guardarlo y verificar su
 * persistencia</li>
 * </ol>
 * 
 * @see <a href="docs/ejercicio-postgres.md">Guía completa del ejercicio</a>
 */
@SpringBootTest
@Testcontainers
public class StockRepositoryIT extends BaseContainersTest {

    // TODO: Declarar los contenedores
    // - LocalStackContainer para SQS
    // - PostgreSQLContainer para la base de datos
    // - GenericContainer para Redis

    // TODO: Añadir bloque static {} para iniciar los contenedores

    // TODO: Añadir @DynamicPropertySource para configurar:
    // - spring.datasource.url, username, password
    // - spring.cloud.aws.* (región, endpoint, credenciales)
    // - spring.data.redis.host, port

    // TODO: Inyectar StockRepository

    @Test
    void ejemplo_prueba_persistencia() {
        // TODO: Implementar el test
        // 1) Crear y guardar un Stock con símbolo "NVDA" y precio 450.00
        // 2) Recuperar el Stock por símbolo usando stockRepository.findBySymbol()
        // 3) Verificar con assertThat que:
        // - El Optional contiene un valor
        // - El símbolo coincide
        // - El precio coincide
        // - El ID no es null (fue generado por la base de datos)
    }
}
