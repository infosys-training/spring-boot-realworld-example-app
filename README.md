# ![RealWorld Example App using Kotlin and Spring](example-logo.png)

[![Actions](https://github.com/gothinkster/spring-boot-realworld-example-app/workflows/Java%20CI/badge.svg)](https://github.com/gothinkster/spring-boot-realworld-example-app/actions)

> ### Spring boot + MyBatis codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld-example-apps) spec and API.

This codebase was created to demonstrate a fully fledged full-stack application built with Spring boot + Mybatis including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# *NEW* GraphQL Support  

Following some DDD principles. REST or GraphQL is just a kind of adapter. And the domain layer will be consistent all the time. So this repository implement GraphQL and REST at the same time.

The GraphQL schema is https://github.com/gothinkster/spring-boot-realworld-example-app/blob/master/src/main/resources/schema/schema.graphqls and the visualization looks like below.

![](graphql-schema.png)

And this implementation is using [dgs-framework](https://github.com/Netflix/dgs-framework) which is a quite new java graphql server framework.
# How it works

The application uses Spring Boot (Web, Mybatis).

* Use the idea of Domain Driven Design to separate the business term and infrastructure term.
* Use MyBatis to implement the [Data Mapper](https://martinfowler.com/eaaCatalog/dataMapper.html) pattern for persistence.
* Use [CQRS](https://martinfowler.com/bliki/CQRS.html) pattern to separate the read model and write model.

And the code is organized as this:

1. `api` is the web layer implemented by Spring MVC
2. `core` is the business model including entities and services
3. `application` is the high-level services for querying the data transfer objects
4. `infrastructure`  contains all the implementation classes as the technique details

# Security

Integration with Spring Security and add other filter for jwt token process.

The secret key is stored in `application.properties` and can be overridden via the `JWT_SECRET` environment variable.

# Database

The application uses **PostgreSQL** for production and **H2 (PostgreSQL compatibility mode)** for testing.

## Sample Data & Login Credentials

The application includes seed data with sample users, articles, tags, comments, and social interactions. You can log in with any of these accounts:

| Username | Email | Password |
|----------|-------|----------|
| johndoe | john@example.com | password123 |
| janedoe | jane@example.com | password123 |
| bobsmith | bob@example.com | password123 |

**Seed data includes:**
- 3 users with profiles
- 5 articles on Spring Boot, REST APIs, Microservices, Docker, and Testing
- 7 tags (java, spring-boot, web-development, tutorial, best-practices, microservices, api-design)
- 5 comments on articles
- 6 article favorites
- 4 follow relationships between users

# Getting started

## Backend (Spring Boot)

You'll need Java 11 installed.

### Local Development with PostgreSQL

1. Start a PostgreSQL instance (e.g., via Docker):

```bash
docker run -d --name realworld-postgres \
  -e POSTGRES_DB=realworld \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:14-alpine
```

2. Run the application:

```bash
./gradlew bootRun
```

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

### Health Check

The application exposes Spring Boot Actuator endpoints:

```bash
curl http://localhost:8080/actuator/health
```

## Frontend (Next.js)

You'll need Node.js installed. **Recommended: Node v14-16** (specified in `frontend/.nvmrc`).

If using `nvm`, switch to the correct version:
```bash
cd frontend
nvm use
```

Then install and run:
```bash
npm install
npm run dev
```

The frontend will run on http://localhost:3000 and connect to the backend on port 8080.

**Note**: The `npm run dev` script includes `NODE_OPTIONS=--openssl-legacy-provider` for compatibility with newer Node versions, but Node 14-16 is still recommended for best compatibility.

# Docker

## Build and Run with Docker

```bash
docker build -t realworld-api .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/realworld \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e JWT_SECRET=your-secret-key \
  realworld-api
```

The Dockerfile uses a multi-stage build with:
- **Build stage**: Gradle 7.4 + JDK 11 for compilation
- **Runtime stage**: Eclipse Temurin JRE 11 Alpine for minimal image size
- Non-root user for security
- Proper layer caching for dependencies

# Amazon EKS Deployment

## Prerequisites

- AWS CLI configured with appropriate permissions
- `kubectl` installed and configured
- An EKS cluster created
- Amazon ECR repository created
- AWS Load Balancer Controller installed on the cluster
- PostgreSQL database (Amazon RDS recommended)

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DATABASE_URL` | PostgreSQL JDBC connection string | Yes |
| `DATABASE_USERNAME` | Database username | Yes |
| `DATABASE_PASSWORD` | Database password | Yes |
| `JWT_SECRET` | Secret key for JWT token signing | Yes |
| `SPRING_PROFILES_ACTIVE` | Spring profile (`prod` for production) | Yes |
| `SERVER_PORT` | Server port (default: 8080) | No |
| `DB_POOL_MAX_SIZE` | HikariCP max pool size (default: 10) | No |
| `DB_POOL_MIN_IDLE` | HikariCP min idle connections (default: 5) | No |

## PostgreSQL Setup (Amazon RDS)

1. Create an RDS PostgreSQL instance:

```bash
aws rds create-db-instance \
  --db-instance-identifier realworld-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 14.7 \
  --master-username postgres \
  --master-user-password <YOUR_PASSWORD> \
  --allocated-storage 20 \
  --vpc-security-group-ids <SECURITY_GROUP_ID> \
  --db-name realworld
```

2. Ensure the RDS security group allows inbound traffic from the EKS cluster's security group on port 5432.

## Deploy to EKS

### 1. Create ECR Repository

```bash
aws ecr create-repository --repository-name realworld-api --region us-east-1
```

### 2. Build and Push Docker Image

```bash
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION=us-east-1

aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

docker build -t realworld-api .
docker tag realworld-api:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/realworld-api:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/realworld-api:latest
```

### 3. Update Kubernetes Manifests

Edit `k8s/configmap.yaml` with your RDS endpoint:
```yaml
data:
  database-url: "jdbc:postgresql://<RDS_ENDPOINT>:5432/realworld"
```

Edit `k8s/secret.yaml` with your credentials:
```yaml
stringData:
  database-username: "postgres"
  database-password: "<YOUR_PASSWORD>"
  jwt-secret: "<YOUR_JWT_SECRET>"
```

Edit `k8s/deployment.yaml` with your ECR image URI:
```yaml
image: <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/realworld-api:latest
```

### 4. Apply Kubernetes Manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

### 5. Verify Deployment

```bash
kubectl get pods -n realworld
kubectl get svc -n realworld
kubectl get ingress -n realworld

kubectl logs -f deployment/realworld-api -n realworld
```

## Troubleshooting

### Check pod status
```bash
kubectl describe pod <POD_NAME> -n realworld
```

### View application logs
```bash
kubectl logs -f deployment/realworld-api -n realworld
```

### Check health endpoint
```bash
kubectl port-forward svc/realworld-api 8080:8080 -n realworld
curl http://localhost:8080/actuator/health
```

### Restart deployment
```bash
kubectl rollout restart deployment/realworld-api -n realworld
```

### Scale deployment manually
```bash
kubectl scale deployment/realworld-api --replicas=3 -n realworld
```

### Check HPA status
```bash
kubectl get hpa -n realworld
```

# CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/gradle.yml`) includes:

1. **Build & Test**: Runs on every push/PR — compiles code and runs unit tests
2. **Docker Build & Push**: On `master` branch or version tags — builds Docker image and pushes to ECR
3. **Deploy to EKS**: On `master` branch or version tags — applies Kubernetes manifests and updates the deployment

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `AWS_ACCESS_KEY_ID` | AWS IAM access key |
| `AWS_SECRET_ACCESS_KEY` | AWS IAM secret key |

# Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test

# Code format

Use spotless for code format.

    ./gradlew spotlessJavaApply

# Help

Please fork and PR to improve the project.
