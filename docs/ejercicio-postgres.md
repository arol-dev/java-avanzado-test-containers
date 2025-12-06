# Ejercicio 1: Prueba de Integración con PostgreSQL

## Objetivo

Aprender a usar **Testcontainers** para ejecutar un contenedor de PostgreSQL durante las pruebas de integración con Spring Boot.

## Archivo a completar

`src/test/java/com/example/stocks/containers/StockRepositoryIT.java`

---

## Paso 1: Añadir las anotaciones de clase

Asegúrate de que la clase tenga las siguientes anotaciones:

```java
@SpringBootTest
@Testcontainers
public class StockRepositoryIT extends BaseContainersTest {
```

- `@SpringBootTest`: Carga el contexto completo de Spring Boot.
- `@Testcontainers`: Activa la gestión automática del ciclo de vida de contenedores.

---

## Paso 2: Declarar los contenedores

La aplicación necesita **PostgreSQL**, **Redis** y **LocalStack (SQS)** para arrancar correctamente. Declara los tres contenedores como campos estáticos:

```java
@SuppressWarnings("resource")
static LocalStackContainer localstack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.4"))
        .withServices(LocalStackContainer.Service.SQS);

@SuppressWarnings("resource")
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

@SuppressWarnings("resource")
static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);
```

> **Nota importante**: Usamos un bloque `static {}` para iniciar los contenedores **antes** de que Spring intente resolver las propiedades dinámicas.

```java
static {
    localstack.start();
    postgres.start();
    redis.start();
}
```

---

## Paso 3: Configurar las propiedades dinámicas

Usa `@DynamicPropertySource` para inyectar las URLs y credenciales de los contenedores en Spring:

```java
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    // PostgreSQL
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    // LocalStack / SQS
    registry.add("spring.cloud.aws.region.static", localstack::getRegion);
    registry.add("spring.cloud.aws.sqs.endpoint", 
        () -> localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
    registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
    registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);

    // Redis
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
}
```

---

## Paso 4: Inyectar el repositorio

Añade el repositorio como dependencia del test:

```java
@Autowired
private StockRepository stockRepository;
```

---

## Paso 5: Implementar el test

Completa el método de prueba `ejemplo_prueba_persistencia()`:

1. **Crear un Stock:**

   ```java
   String symbol = "NVDA";
   BigDecimal price = new BigDecimal("450.00");
   Stock stock = new Stock(symbol, price, Instant.now());
   ```

2. **Guardar en la base de datos:**

   ```java
   stockRepository.save(stock);
   ```

3. **Recuperar por símbolo:**

   ```java
   Optional<Stock> found = stockRepository.findBySymbol(symbol);
   ```

4. **Hacer aserciones:**

   ```java
   assertThat(found).isPresent();
   assertThat(found.get().getSymbol()).isEqualTo(symbol);
   assertThat(found.get().getPrice()).isEqualByComparingTo(price);
   assertThat(found.get().getId()).isNotNull();
   ```

---

## Verificación

Ejecuta el test con:

```bash
mvn -Dtest=StockRepositoryIT verify
```

El test debe pasar y verás en los logs cómo Testcontainers levanta los contenedores automáticamente.

---

## Conceptos clave aprendidos

| Concepto | Descripción |
|----------|-------------|
| `PostgreSQLContainer` | Contenedor especializado para PostgreSQL con métodos como `getJdbcUrl()` |
| `@DynamicPropertySource` | Inyecta propiedades en tiempo de ejecución desde los contenedores |
| Bloque `static {}` | Garantiza que los contenedores estén listos antes del contexto de Spring |
| `@SpringBootTest` | Carga el contexto completo, necesario para JPA |

---

## Imports necesarios

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;
```
