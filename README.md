# Order Notification System

A backend project demonstrating **event-driven microservices** architecture using Java, Spring Boot, RabbitMQ, and JWT authentication. Built to explore asynchronous inter-service communication, stateless authentication, message brokers, and the Database-per-Service pattern.

---

## What it does

A client registers and logs in via the Auth Service, receiving a JWT token. They use that token to create orders via the Order Service, which saves the order and publishes an event to RabbitMQ. The Notification Service independently listens for that event and persists a notification record — without either service knowing about the other's internals.

```
[Auth Service] ──── issues JWT ────────────────────────────────────► Client
                                                                        │
                                                               Bearer token on requests
                                                                        │
                                                                        ▼
Client ──── POST /orders + JWT ──► [Order Service] ──► [RabbitMQ] ──► [Notification Service]
                                         │                                      │
                                    orders_db                          notifications_db
```

---

## Why this architecture?

This project intentionally avoids direct service-to-service calls. Instead, services communicate through a message broker and a shared JWT secret, which means:

- **Order Service can't break Notification Service** — if notifications go down, orders still work
- **Auth is fully decoupled** — any service can validate JWT tokens locally without calling Auth Service
- **Easy to scale independently** — add more notification consumers without touching order logic
- **New services can tap in** — an Email Service or SMS Service could subscribe to the same queue with zero changes to Order Service

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3 |
| **Authentication** | Spring Security + JWT (jjwt) |
| **Message Broker** | RabbitMQ (Topic Exchange) |
| **Database** | PostgreSQL (one per service) |
| **ORM** | Spring Data JPA / Hibernate |
| **Infrastructure** | Docker Compose |
| **Build Tool** | Maven |

---

## Key Implementation Details

**Stateless JWT authentication** — Auth Service issues signed JWT tokens. Order Service validates them locally using a shared secret — no database call or inter-service request needed per request.

**BCrypt password hashing** — passwords are never stored in plain text. Spring Security's `BCryptPasswordEncoder` hashes on register and verifies on login.

**JWT filter chain** — a custom `JwtAuthFilter` extends `OncePerRequestFilter` and intercepts every request to Order Service, validating the token before Spring Security processes it.

**RabbitMQ setup** — uses a Topic Exchange with explicit queue/binding configuration and a Dead Letter Queue (DLQ) to catch failed messages, with configurable retry limits.

**DTO pattern** — API layer uses request/response DTOs, keeping JPA entities internal and preventing over-posting attacks.

**@PrePersist hooks** — `status` and `createdAt` fields are set automatically at the persistence layer.

**JSON serialization** — messages are serialized as JSON (not Java serialization), making them language-agnostic and human-readable in the RabbitMQ dashboard.

**Database isolation** — each service owns its schema (`auth_db`, `orders_db`, `notifications_db`), enforcing the Database-per-Service principle.

**Secret management** — JWT secrets are stored in `.env` files (gitignored) and loaded at startup via `dotenv-java`, never hardcoded in source.

---

## Running Locally

**Prerequisites:** JDK 21, Docker Desktop, IntelliJ IDEA

```bash
# 1. Clone
git clone https://github.com/your-username/order-notification-system.git
cd order-notification-system

# 2. Start infrastructure
docker compose up -d

# 3. Create the required databases
docker exec -it <postgres-container> psql -U myuser -d postgres -c "CREATE DATABASE notifications_db;"
docker exec -it <postgres-container> psql -U myuser -d postgres -c "CREATE DATABASE auth_db;"

# 4. Set up .env files
cp auth-service/.env.example auth-service/.env
cp order-service/.env.example order-service/.env
# Fill in the same JWT_SECRET in both files
# Generate one with: openssl rand -base64 32

# 5. Run all three services in IntelliJ
# AuthServiceApplication         → localhost:8082
# OrderServiceApplication        → localhost:8080
# NotificationServiceApplication → localhost:8081
```

---

## Usage Flow

**1. Register:**
```bash
curl -X POST http://localhost:8082/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@gmail.com", "password": "password123"}'
```

**2. Login and get token:**
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@gmail.com", "password": "password123"}'
```
```json
{ "token": "eyJhbGci..." }
```

**3. Create an order using the token:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGci..." \
  -d '{"product": "Laptop", "quantity": 1, "price": 999.99}'
```
```json
{
  "id": 1,
  "product": "Laptop",
  "quantity": 1,
  "price": 999.99,
  "status": "PENDING",
  "createdAt": "2026-04-28T20:07:25"
}
```

Behind the scenes, Notification Service receives the event from RabbitMQ and saves a notification record to its own database.

**Monitor RabbitMQ:** `http://localhost:15672` → login: `user` / `password`

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## Project Structure

```
order-notification-system/
├── docker-compose.yml
├── auth-service/
│   └── src/main/java/
│       ├── controller/    # /auth/register, /auth/login
│       ├── dto/           # UserRequest, UserResponse
│       ├── model/         # User JPA entity
│       ├── repository/    # Spring Data JPA
│       ├── security/      # JwtUtil, SecurityConfig
│       └── service/       # Auth logic, BCrypt, token generation
├── order-service/
│   └── src/main/java/
│       ├── config/        # RabbitMQ exchange, queue, binding, DLQ
│       ├── controller/    # REST endpoints
│       ├── dto/           # OrderRequest, OrderResponse
│       ├── exception/     # Global exception handler
│       ├── model/         # Order JPA entity
│       ├── repository/    # Spring Data JPA
│       ├── security/      # JwtAuthFilter, JwtUtil, SecurityConfig
│       └── service/       # Business logic + event publishing
└── notification-service/
    └── src/main/java/
        ├── config/        # RabbitMQ consumer configuration + DLQ
        ├── listener/      # @RabbitListener event handler
        ├── model/         # Notification JPA entity
        └── repository/    # Spring Data JPA
```

---

## API Reference

### Auth Service — `localhost:8082`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a new user |
| `POST` | `/auth/login` | Public | Login and receive JWT token |

### Order Service — `localhost:8080`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/orders` | 🔒 JWT | Create an order, triggers notification event |
| `GET` | `/orders` | 🔒 JWT | List all orders |

---

## What I learned building this

- How stateless JWT authentication works and why it's suited for microservices
- How message brokers decouple services and why that matters at scale
- The difference between synchronous REST calls between services vs async event publishing
- Spring Security filter chain — how requests are intercepted and authenticated
- Why Database-per-Service prevents tight coupling even at the data layer
- Dead Letter Queues and retry strategies for resilient message processing
- Secret management with `.env` files and why secrets should never be in source code
- Debugging serialization issues between services communicating over a queue