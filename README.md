# Order Notification System

A backend project demonstrating **event-driven microservices** architecture using Java, Spring Boot, and RabbitMQ. Built to explore asynchronous inter-service communication, message brokers, and the Database-per-Service pattern.

---

## What it does

When a client creates an order via REST API, the Order Service saves it to its database and publishes an event to RabbitMQ. The Notification Service independently listens for that event and persists a notification record — without either service knowing about the other's internals.

```
Client → POST /orders → Order Service → RabbitMQ → Notification Service
                              │                              │
                         orders_db                  notifications_db
```

---

## Why this architecture?

This project intentionally avoids direct service-to-service calls (REST or gRPC between services). Instead, services communicate through a message broker, which means:

- **Order Service can't break Notification Service** — if notifications go down, orders still work
- **Easy to scale independently** — add more notification consumers without touching order logic
- **New services can tap in** — an Email Service or SMS Service could subscribe to the same queue with zero changes to Order Service

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3 |
| **Message Broker** | RabbitMQ (Topic Exchange) |
| **Database** | PostgreSQL (one per service) |
| **ORM** | Spring Data JPA / Hibernate |
| **Infrastructure** | Docker Compose |
| **Build Tool** | Maven |

---

## Key Implementation Details

**RabbitMQ setup** — uses a Topic Exchange with explicit queue/binding configuration, allowing routing key-based message filtering if the system grows.

**DTO pattern** — API layer uses `OrderRequest` / `OrderResponse` DTOs, keeping JPA entities internal and preventing over-posting attacks.

**@PrePersist hooks** — `status` and `createdAt` fields are set automatically at the persistence layer, not at the controller or service level.

**JSON serialization** — messages are serialized as JSON (not Java serialization), making them language-agnostic and human-readable in the RabbitMQ dashboard.

**Database isolation** — each service owns its schema. `orders_db` and `notifications_db` are separate PostgreSQL databases, enforcing the Database-per-Service principle.

---

## Running Locally

**Prerequisites:** JDK 21, Docker Desktop, IntelliJ IDEA

```bash
# 1. Clone
git clone https://github.com/your-username/order-notification-system.git
cd order-notification-system

# 2. Start infrastructure
docker compose up -d

# 3. Create the notifications database
docker exec -it <postgres-container> psql -U myuser -d postgres -c "CREATE DATABASE notifications_db;"

# 4. Run both services in IntelliJ
# OrderServiceApplication        → localhost:8080
# NotificationServiceApplication → localhost:8081
```

**Create an order:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"product": "Laptop", "quantity": 1, "price": 999.99}'
```

**Expected response:**
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

---

## Project Structure

```
order-notification-system/
├── docker-compose.yml
├── order-service/
│   └── src/main/java/
│       ├── config/        # RabbitMQ exchange, queue, binding
│       ├── controller/    # REST endpoints
│       ├── dto/           # OrderRequest, OrderResponse
│       ├── model/         # Order JPA entity
│       ├── repository/    # Spring Data JPA
│       └── service/       # Business logic + event publishing
└── notification-service/
    └── src/main/java/
        ├── config/        # RabbitMQ consumer configuration
        ├── listener/      # @RabbitListener event handler
        ├── model/         # Notification JPA entity
        └── repository/    # Spring Data JPA
```

---

## API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/orders` | Create an order, triggers notification event |
| `GET` | `/orders` | List all orders |
