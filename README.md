# LocalNow

LocalNow is an on-demand marketplace for finding nearby, trusted professionals who are genuinely available when a service is needed.

The initial product is focused on urgent and same-day home repairs in Madrid, starting with plumbing, electrical work, and locksmith services.

> **Status:** product definition and technical foundation.

## Why LocalNow?

Finding a professional is easy. Finding the right professional who is nearby, qualified, and available at the requested time is not.

LocalNow turns a structured service request into an explainable ranking of eligible professionals based on availability, distance, service fit, reputation, and budget compatibility.

## Initial User Flow

```text
Customer creates a service request
              ↓
Location + category + time + budget
              ↓
Geospatial candidate search
              ↓
Explainable matching and ranking
              ↓
Professional accepts
              ↓
Job + chat + notifications
              ↓
Payment + review
```

## Planned Architecture

LocalNow starts as a modular monolith. Microservices and additional infrastructure will be introduced only when scale or operational constraints justify them.

```text
Android app
Kotlin + Jetpack Compose
          │
          │ REST / WebSocket
          ▼
Kotlin + Spring Boot
          │
          ├── PostgreSQL + PostGIS
          ├── Redis (when reservation use cases require it)
          ├── Object storage
          └── OpenTelemetry
```

## Technology Stack

### Android

- Kotlin
- Jetpack Compose and Material 3
- Coroutines and Flow
- Hilt
- Retrofit and OkHttp
- Room and DataStore
- Google Maps SDK
- Firebase Cloud Messaging

### Backend

- Kotlin and Java 21
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Bean Validation
- Flyway
- OpenAPI
- WebSockets

### Data and Infrastructure

- PostgreSQL with PostGIS
- Redis for explicit caching and temporary-reservation use cases
- Docker and Docker Compose
- GitHub Actions
- OpenTelemetry, Prometheus, and Grafana

### Testing

- JUnit 5
- MockK
- Testcontainers
- Android unit and Compose UI tests

## Repository Layout

```text
localnow/
├── android/       # Native Android application
├── backend/       # Kotlin/Spring Boot modular monolith
├── docs/          # Product and technical documentation
└── infra/         # Local and deployment infrastructure
```

## Product Scope

The initial scope, hypotheses, exclusions, metrics, and validation plan are documented in [docs/PRODUCT.md](docs/PRODUCT.md).

## Current Roadmap

1. Validate the problem with professionals in Madrid.
2. Bootstrap the Kotlin/Spring Boot backend.
3. Run PostgreSQL with PostGIS locally.
4. Model users, professionals, services, availability, and requests.
5. Implement geospatial candidate search.
6. Add explainable matching and ranking.
7. Build the first Android customer flow in Jetpack Compose.
8. Complete one end-to-end job lifecycle.

## Engineering Principles

- Organize code by business domain.
- Prefer a modular monolith for the initial product.
- Make state transitions explicit.
- Protect booking acceptance with transactions and idempotency.
- Add infrastructure to solve demonstrated product problems.
- Test integrations against real dependencies with Testcontainers.
- Keep observability part of the product from the beginning.

## License

This project is licensed under the MIT License.
