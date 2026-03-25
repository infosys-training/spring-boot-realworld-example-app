# Bank Customer Onboarding Platform

A microservices-based bank customer onboarding application built with **Java 17 + Spring Boot 3**, **React 18 + TypeScript**, and **Docker Compose** for local deployment.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌──────────────────┐
│   React UI  │────▶│  API Gateway │────▶│ Discovery Server │
│  (port 3000)│     │  (port 8080) │     │   (Eureka 8761)  │
└─────────────┘     └──────┬───────┘     └──────────────────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ┌────────────┐ ┌───────────┐ ┌─────────────────┐
     │  Customer   │ │    KYC    │ │  Notification   │
     │  Service    │ │  Service  │ │    Service      │
     │ (port 8081) │ │(port 8082)│ │  (port 8083)    │
     └─────┬──────┘ └─────┬─────┘ └───────┬─────────┘
           │              │               │
     ┌─────▼──────┐ ┌─────▼─────┐ ┌───────▼─────────┐
     │ PostgreSQL  │ │ PostgreSQL│ │   PostgreSQL    │
     │ (port 5432) │ │(port 5433)│ │  (port 5434)    │
     └────────────┘ └───────────┘ └─────────────────┘

              ┌──────────────┐     ┌───────────┐
              │   RabbitMQ   │     │  MailHog   │
              │ (port 5672)  │     │(port 8025) │
              └──────────────┘     └───────────┘
```

### Microservices

| Service | Port | Description |
|---------|------|-------------|
| **Discovery Server** | 8761 | Eureka service registry for service discovery |
| **API Gateway** | 8080 | Spring Cloud Gateway - routes requests to microservices |
| **Customer Service** | 8081 | Customer CRUD, onboarding workflow, publishes KYC events |
| **KYC Service** | 8082 | KYC verification pipeline (identity, document, sanctions, PEP) |
| **Notification Service** | 8083 | Email notifications via MailHog, audit logging |
| **Frontend** | 3000 | React SPA with multi-step onboarding form |

### Event-Driven Flow

1. Customer submits onboarding form via React UI
2. Customer Service creates record and publishes `KycRequestEvent` to RabbitMQ
3. KYC Service consumes the event, runs 4 verification checks (identity, document, sanctions, PEP)
4. KYC Service publishes `KycResultEvent` (to update customer status) and `NotificationEvent` (to send email)
5. Customer Service updates onboarding status based on KYC result
6. Notification Service sends email via MailHog and logs to audit database

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) (v20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)

## How to Run

```bash
cd bank-customer-onboarding
docker-compose up --build
```

Wait for all services to start (this may take a few minutes on the first build).

## Service URLs

| Service | URL | Notes |
|---------|-----|-------|
| **Frontend** | http://localhost:3000 | React onboarding UI |
| **API Gateway** | http://localhost:8080 | REST API entry point |
| **Eureka Dashboard** | http://localhost:8761 | Service registry dashboard |
| **RabbitMQ Management** | http://localhost:15672 | Username: `guest` / Password: `guest` |
| **MailHog UI** | http://localhost:8025 | View sent email notifications |

## API Documentation (Swagger UI)

| Service | Swagger URL |
|---------|-------------|
| Customer Service | http://localhost:8081/swagger-ui.html |
| KYC Service | http://localhost:8082/swagger-ui.html |
| Notification Service | http://localhost:8083/swagger-ui.html |

## API Endpoints

### Customer Service (`/api/customers`)
- `POST /api/customers` - Create new customer (triggers KYC verification)
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/{id}/status` - Get onboarding status
- `PUT /api/customers/{id}` - Update customer details
- `GET /api/customers` - List customers (paginated)

### KYC Service (`/api/kyc`)
- `GET /api/kyc/{customerId}` - Get KYC status for customer
- `GET /api/kyc/{customerId}/checks` - Get individual check results
- `POST /api/kyc/{customerId}/retry` - Retry failed KYC verification

### Notification Service (`/api/notifications`)
- `GET /api/notifications/{customerId}` - List notifications for a customer

## Technology Stack

### Backend
- Java 17, Spring Boot 3.2.1, Spring Cloud 2023.0.0
- Spring Data JPA, PostgreSQL, Flyway migrations
- Spring AMQP (RabbitMQ), Spring Mail (MailHog)
- Spring Cloud Gateway, Netflix Eureka
- Lombok, MapStruct, Jakarta Validation
- SpringDoc OpenAPI (Swagger)

### Frontend
- React 18, TypeScript, Vite
- Material UI (MUI), React Router v6
- Formik + Yup (form validation)
- TanStack React Query, Axios

### Infrastructure
- Docker, Docker Compose
- PostgreSQL 15, RabbitMQ 3, MailHog, Nginx

## Project Structure

```
bank-customer-onboarding/
├── services/
│   ├── customer-service/       # Customer CRUD + KYC event publishing
│   ├── kyc-service/            # KYC verification pipeline
│   ├── notification-service/   # Email notifications + audit
│   ├── api-gateway/            # Spring Cloud Gateway
│   └── discovery-server/       # Eureka Server
├── frontend/                   # React + TypeScript SPA
├── docker-compose.yml          # Full stack orchestration
└── README.md
```

## Stopping the Application

```bash
docker-compose down
```

To also remove data volumes:

```bash
docker-compose down -v
```
