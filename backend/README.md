# Backend application

Kotlin and Spring Boot modular monolith for LocalNow.

## Requirements

- Java 21
- Docker with Compose

The Gradle wrapper is included, so a system Gradle installation is not required.

## Run locally

From the repository root:

```bash
docker compose up -d postgres
cd backend
./gradlew bootRun
```

The application health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

Stop the database without deleting its volume:

```bash
docker compose stop postgres
```

## Test

Integration tests start an isolated PostGIS database through Testcontainers:

```bash
./gradlew test
```

## Database migrations

Flyway migrations live in `src/main/resources/db/migration`. Hibernate validates
the mapped schema but does not create or modify it.
