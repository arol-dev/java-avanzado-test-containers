# Ejercicio 2: Prueba de Integración con Redis

## Objetivo

Aprender a usar **Testcontainers** con un `GenericContainer` para ejecutar Redis durante las pruebas de integración con Spring Boot.

## Archivo a completar

`src/test/java/com/example/stocks/containers/RedisCacheIT.java`

---

## Paso 1: Añadir las anotaciones de clase

Esta prueba usa una configuración **mínima** con `@DataRedisTest` en lugar de `@SpringBootTest`:

```java
@DataRedisTest
@Import(StockCacheService.class)
@Testcontainers
@ActiveProfiles("test")
class RedisCacheIT {
```

| Anotación | Propósito |
|-----------|-----------|
| `@DataRedisTest` | Carga solo componentes de Spring Data Redis (contexto mínimo) |
| `@Import` | Importa manualmente el servicio que queremos probar |
| `@Testcontainers` | Activa la gestión de contenedores |
| `@ActiveProfiles("test")` | Usa el perfil de test |

---

## Paso 2: Declarar el contenedor Redis

Usa `GenericContainer` con la imagen de Redis:

```java
@SuppressWarnings("resource")
@Container
static final GenericContainer<?> redis = new GenericContainer<>(
        DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);
```

> **Nota**: Usamos `@Container` porque `@DataRedisTest` no tiene las mismas restricciones de timing que `@SpringBootTest`. Testcontainers iniciará el contenedor automáticamente.

---

## Paso 3: Configurar las propiedades dinámicas

Redis necesita host y puerto dinámicos:

```java
@DynamicPropertySource
static void redisProps(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
}
```

> **Importante**: `getMappedPort(6379)` devuelve el puerto real asignado por Docker, que puede ser diferente a 6379.

---

## Paso 4: Inyectar el servicio de caché

```java
@Autowired
private StockCacheService cache;
```

---

## Paso 5: Implementar el test

Completa el método `putCache()`:

1. **Guardar un valor en caché:**

   ```java
   cache.put("GOOG", BigDecimal.TEN);
   ```

2. **Verificar que se puede recuperar:**

   ```java
   assertThat(cache.get("GOOG")).hasValue(BigDecimal.TEN);
   ```

### Test adicional sugerido: verificar expiración

Añade un test para verificar el comportamiento cuando la clave no existe:

```java
@Test
void getReturnEmptyWhenKeyNotExists() {
    assertThat(cache.get("UNKNOWN")).isEmpty();
}
```

---

## Verificación

Ejecuta el test con:

```bash
mvn -Dtest=RedisCacheIT verify
```

---

## Conceptos clave aprendidos

| Concepto | Descripción |
|----------|-------------|
| `GenericContainer` | Contenedor genérico para cualquier imagen Docker |
| `withExposedPorts()` | Expone puertos del contenedor a un puerto aleatorio del host |
| `getMappedPort()` | Obtiene el puerto real asignado en el host |
| `@DataRedisTest` | Slice test para Redis (contexto mínimo, más rápido) |

---

## Diferencia entre @SpringBootTest y @DataRedisTest

| Aspecto | `@SpringBootTest` | `@DataRedisTest` |
|---------|-------------------|------------------|
| Contexto | Completo | Mínimo (solo Redis) |
| Velocidad | Más lento | Más rápido |
| Componentes cargados | Todos | Solo Spring Data Redis |
| Uso típico | Pruebas E2E | Pruebas unitarias de caché |

---

## Imports necesarios

```java
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
```
