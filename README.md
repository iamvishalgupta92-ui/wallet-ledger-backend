# Wallet Ledger — Backend

A backend service for a digital wallet and transaction management system built with Spring Boot.

The application provides REST APIs for user management, wallet operations, and transaction processing, with PostgreSQL used for persistent data storage.

## Features

- User registration and management
- JWT-based authentication and authorization
- Wallet management
- Wallet balance tracking
- Money transfer between wallets
- Transaction creation and history
- RESTful APIs
- PostgreSQL database integration
- Redis integration
- Apache Kafka integration for event-driven transaction processing
- Spring Data JPA / Hibernate
- Docker support

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Backend development |
| Spring Boot | Application framework |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Persistent database |
| Redis | Caching / fast data access |
| Apache Kafka | Event-driven processing |
| Spring Security | Authentication & authorization |
| JWT | Token-based authentication |
| Maven | Dependency management |
| Docker | Containerization |

## Architecture

```text
Client / Frontend
       |
       v
  REST API
       |
       v
 Spring Boot
       |
  +----+----------------+
  |    |                |
  v    v                v
JPA  Redis            Kafka
  |                     |
  v                     v
PostgreSQL          Transaction Events
