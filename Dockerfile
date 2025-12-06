## Etapa 1: build con Maven y JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiamos los descriptores primero para aprovechar la caché de dependencias
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline

# Copiamos el código y compilamos
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests package

## Etapa 2: imagen de runtime con JRE 21
FROM eclipse-temurin:21-jre
ENV JAVA_OPTS=""
WORKDIR /app

# Copiamos el JAR construido
COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

# Nota: las variables de entorno (DB_*, REDIS_*, AWS_*, SQS_QUEUE_NAME) se inyectan vía docker-compose
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
