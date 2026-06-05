# Spring Boot RealWorld Example App — Documentation

> A reference backend implementation of the [RealWorld](https://github.com/gothinkster/realworld) "Conduit" blogging specification, built with **Spring Boot**, **MyBatis**, and **Netflix DGS** (GraphQL). It provides both REST and GraphQL APIs for social blogging features including articles, user profiles, comments, favorites, and follower feeds.

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Getting Started](#getting-started)
6. [Database](#database)
7. [Authentication & Security](#authentication--security)
8. [REST API Reference](#rest-api-reference)
9. [GraphQL API Reference](#graphql-api-reference)
10. [Testing](#testing)
11. [Code Formatting](#code-formatting)
12. [Docker](#docker)
13. [Frontend (Next.js)](#frontend-nextjs)
14. [CI/CD](#cicd)
15. [Configuration Reference](#configuration-reference)

---

## Overview

This application demonstrates a fully fledged full-stack application built with **Spring Boot + MyBatis**, including CRUD operations, JWT authentication, pagination, and more — all adhering to the [RealWorld spec](https://github.com/gothinkster/realworld).

Key highlights:

- **Dual API surface**: REST endpoints _and_ GraphQL (via Netflix DGS) over the same domain layer.
- **Domain-Driven Design (DDD)**: Clean separation between domain (core), application services, and infrastructure.
- **CQRS pattern**: Distinct read models (query services) and write models (command services / repositories).
- **Data Mapper pattern**: MyBatis maps SQL results directly to DTOs — no ORM overhead.
- **Stateless JWT authentication**: The `Authorization: Token <jwt>` header is the sole authentication mechanism.

---

## Architecture

The codebase follows a layered DDD architecture:

```
┌──────────────────────────────────────────────────────────────────┐
│                        Adapters (Web Layer)                      │
│  ┌────────────────────┐          ┌─────────────────────────┐     │
│  │   REST Controllers │          │  GraphQL Data Fetchers  │     │
│  │     (io.spring.api)│          │   (io.spring.graphql)   │     │
│  └────────┬───────────┘          └──────────┬──────────────┘     │
├───────────┼──────────────────────────────────┼───────────────────┤
│           │       Application Services       │                   │
│           │    (io.spring.application)        │                   │
│  ┌────────▼──────────────────────────────────▼──────────────┐    │
│  │  Query Services (Read)  │  Command Services (Write)      │    │
│  │  ArticleQueryService    │  ArticleCommandService         │    │
│  │  CommentQueryService    │  UserService                   │    │
│  │  ProfileQueryService    │                                │    │
│  │  UserQueryService       │                                │    │
│  │  TagsQueryService       │                                │    │
│  └────────┬────────────────┴─────────────────┬──────────────┘    │
├───────────┼──────────────────────────────────┼───────────────────┤
│           │          Domain (Core)           │                   │
│  ┌────────▼──────────────────────────────────▼──────────────┐    │
│  │  Entities: User, Article, Comment, Tag, FollowRelation,  │    │
│  │           ArticleFavorite                                │    │
│  │  Repositories (interfaces): UserRepository,              │    │
│  │           ArticleRepository, CommentRepository,          │    │
│  │           ArticleFavoriteRepository                      │    │
│  │  Services: JwtService (interface), AuthorizationService  │    │
│  └────────┬─────────────────────────────────────────────────┘    │
├───────────┼──────────────────────────────────────────────────────┤
│           │         Infrastructure                               │
│  ┌────────▼─────────────────────────────────────────────────┐    │
│  │  MyBatis Mappers     (SQL execution)                      │    │
│  │  MyBatis ReadServices (optimized read queries)            │    │
│  │  DefaultJwtService    (JWT implementation via jjwt)       │    │
│  │  Flyway Migrations   (schema versioning)                  │    │
│  │  SQLite               (embedded database)                 │    │
│  └──────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────┘
```

### Design Principles

| Principle | How it is applied |
|---|---|
| **DDD** | Domain entities in `core` have no infrastructure dependencies; repository interfaces define contracts |
| **CQRS** | Write operations go through `ArticleCommandService`/`UserService`; reads go through `*QueryService` classes |
| **Data Mapper** | MyBatis XML mappers (`src/main/resources/mapper/*.xml`) map SQL directly to data objects |
| **Hexagonal / Ports & Adapters** | REST and GraphQL are interchangeable adapters over the same domain + application layer |

---

## Technology Stack

| Category | Technology | Version |
|---|---|---|
| Language | Java | 11 |
| Framework | Spring Boot | 2.6.3 |
| Security | Spring Security | (managed by Boot) |
| Persistence | MyBatis | 2.2.2 |
| Database | SQLite | 3.36.0.3 |
| Migrations | Flyway | (managed by Boot) |
| GraphQL | Netflix DGS | 4.9.21 |
| JWT | jjwt | 0.11.2 |
| Date/Time | Joda-Time | 2.10.13 |
| Build | Gradle | 7.x (wrapper) |
| Code Generation | Lombok | (managed by Boot) |
| Code Format | Spotless (Google Java Format) | 6.2.1 |
| Coverage | JaCoCo | 0.8.7 |
| REST Testing | REST Assured | 4.5.1 |
| E2E Testing | Selenium + TestNG | 4.15.0 / 7.8.0 |
| Frontend | Next.js + React | 9.x / 16.x |

---

## Project Structure

```
spring-boot-realworld-example-app/
├── build.gradle                        # Build configuration, plugins, dependencies
├── gradlew / gradlew.bat               # Gradle wrapper scripts
├── src/
│   ├── main/
│   │   ├── java/io/spring/
│   │   │   ├── RealWorldApplication.java          # Spring Boot entry point
│   │   │   ├── Util.java                          # Shared utilities
│   │   │   ├── api/                               # REST controllers
│   │   │   │   ├── ArticlesApi.java               #   POST /articles, GET /articles, GET /articles/feed
│   │   │   │   ├── ArticleApi.java                #   GET/PUT/DELETE /articles/{slug}
│   │   │   │   ├── ArticleFavoriteApi.java        #   POST/DELETE /articles/{slug}/favorite
│   │   │   │   ├── CommentsApi.java               #   POST/GET/DELETE /articles/{slug}/comments
│   │   │   │   ├── CurrentUserApi.java            #   GET/PUT /user
│   │   │   │   ├── ProfileApi.java                #   GET /profiles/{username}, POST/DELETE follow
│   │   │   │   ├── TagsApi.java                   #   GET /tags
│   │   │   │   ├── UsersApi.java                  #   POST /users, POST /users/login
│   │   │   │   ├── exception/                     #   Custom exceptions & global error handler
│   │   │   │   └── security/                      #   WebSecurityConfig, JwtTokenFilter
│   │   │   ├── application/                       # Application services & DTOs
│   │   │   │   ├── ArticleQueryService.java       #   Article read operations
│   │   │   │   ├── CommentQueryService.java       #   Comment read operations
│   │   │   │   ├── ProfileQueryService.java       #   Profile read operations
│   │   │   │   ├── UserQueryService.java          #   User read operations
│   │   │   │   ├── TagsQueryService.java          #   Tag read operations
│   │   │   │   ├── article/                       #   Article command service, params, validators
│   │   │   │   ├── user/                          #   User service, register/update params
│   │   │   │   ├── data/                          #   DTOs (ArticleData, UserData, ProfileData, etc.)
│   │   │   │   ├── CursorPager.java               #   Cursor-based pagination (GraphQL)
│   │   │   │   ├── DateTimeCursor.java            #   Timestamp-based cursor encoding
│   │   │   │   └── Page.java                      #   Offset-based pagination (REST)
│   │   │   ├── core/                              # Domain layer
│   │   │   │   ├── article/                       #   Article, Tag entities; ArticleRepository interface
│   │   │   │   ├── comment/                       #   Comment entity; CommentRepository interface
│   │   │   │   ├── favorite/                      #   ArticleFavorite entity; ArticleFavoriteRepository
│   │   │   │   ├── user/                          #   User, FollowRelation entities; UserRepository
│   │   │   │   └── service/                       #   JwtService (interface), AuthorizationService
│   │   │   ├── graphql/                           # GraphQL adapters (Netflix DGS)
│   │   │   │   ├── ArticleDatafetcher.java        #   Article queries
│   │   │   │   ├── ArticleMutation.java           #   Article mutations
│   │   │   │   ├── CommentDatafetcher.java        #   Comment queries
│   │   │   │   ├── CommentMutation.java           #   Comment mutations
│   │   │   │   ├── MeDatafetcher.java             #   Current user query
│   │   │   │   ├── ProfileDatafetcher.java        #   Profile queries
│   │   │   │   ├── RelationMutation.java          #   Follow/unfollow mutations
│   │   │   │   ├── TagDatafetcher.java            #   Tag queries
│   │   │   │   ├── UserMutation.java              #   User create/update/login mutations
│   │   │   │   ├── SecurityUtil.java              #   Extract current user from context
│   │   │   │   └── exception/                     #   GraphQL error handlers
│   │   │   └── infrastructure/                    # Technical implementations
│   │   │       ├── mybatis/
│   │   │       │   ├── mapper/                    #   MyBatis write mappers (Java interfaces)
│   │   │       │   └── readservice/               #   MyBatis read services (Java interfaces)
│   │   │       └── service/
│   │   │           └── DefaultJwtService.java     #   JWT token generation & validation
│   │   └── resources/
│   │       ├── application.properties             # Main config (SQLite, JWT, MyBatis)
│   │       ├── application-test.properties        # Test config (in-memory SQLite)
│   │       ├── db/migration/
│   │       │   ├── V1__create_tables.sql          # Schema DDL
│   │       │   └── V2__seed_data.sql              # Sample data (users, articles, tags, etc.)
│   │       ├── mapper/                            # MyBatis XML mapping files
│   │       └── schema/
│   │           └── schema.graphqls                # GraphQL schema definition
│   └── test/
│       └── java/io/spring/
│           ├── api/                               # REST API integration tests (REST Assured + MockMvc)
│           ├── application/                       # Application service unit tests
│           ├── core/                              # Domain entity unit tests
│           ├── infrastructure/                    # Repository integration tests
│           └── selenium/                          # E2E browser tests (Selenium + TestNG)
├── frontend/                                      # Next.js frontend application
│   ├── pages/                                     # Next.js pages
│   ├── components/                                # React components
│   ├── lib/                                       # API client utilities
│   └── package.json                               # Node.js dependencies
└── .github/workflows/gradle.yml                   # CI pipeline (GitHub Actions)
```

---

## Getting Started

### Prerequisites

- **Java 11** (required — Java 17+ breaks Spotless/GoogleJavaFormat)
- **Gradle** (the wrapper `./gradlew` is included, no global install needed)
- **Node.js 14–16** (only if running the frontend)

### Run the Backend

```bash
./gradlew bootRun
```

> **Note**: `bootRun` automatically deletes and recreates `dev.db` with seed data on every run to avoid Flyway migration conflicts.

The backend starts on **http://localhost:8080**. Verify with:

```bash
curl http://localhost:8080/tags
```

### Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on **http://localhost:3000** and connects to the backend at port 8080.

---

## Database

### Engine

The application uses **SQLite** as the embedded database (`dev.db` file in the project root). This can be changed in `application.properties` by modifying the `spring.datasource.*` properties.

### Schema

Managed by **Flyway** with versioned migration scripts in `src/main/resources/db/migration/`:

| Migration | Description |
|---|---|
| `V1__create_tables.sql` | Creates all tables: `users`, `articles`, `article_favorites`, `follows`, `tags`, `article_tags`, `comments` |
| `V2__seed_data.sql` | Inserts sample users, articles, tags, comments, favorites, and follow relationships |

### Entity-Relationship Diagram

```
┌──────────┐       ┌──────────────────┐       ┌──────────┐
│  users   │──1:N──│    articles      │──1:N──│ comments │
│          │       │                  │       │          │
│ id (PK)  │       │ id (PK)          │       │ id (PK)  │
│ username │       │ user_id (FK)     │       │ body     │
│ email    │       │ slug (UNIQUE)    │       │ article_id│
│ password │       │ title            │       │ user_id  │
│ bio      │       │ description      │       │ created_at│
│ image    │       │ body             │       │ updated_at│
└────┬─────┘       │ created_at       │       └──────────┘
     │             │ updated_at       │
     │             └────────┬─────────┘
     │                      │
     │    ┌─────────────────┼──────────────────┐
     │    │                 │                   │
     │    ▼                 ▼                   ▼
┌────┴────────┐   ┌────────────────┐   ┌──────────────┐
│   follows   │   │article_favorites│  │ article_tags │
│             │   │                │   │              │
│ user_id     │   │ article_id     │   │ article_id   │
│ follow_id   │   │ user_id        │   │ tag_id       │
└─────────────┘   └────────────────┘   └──────┬───────┘
                                               │
                                        ┌──────▼───────┐
                                        │    tags      │
                                        │              │
                                        │ id (PK)      │
                                        │ name         │
                                        └──────────────┘
```

### Seed Data

The application ships with sample data for development. Login credentials:

| Username | Email | Password |
|---|---|---|
| johndoe | john@example.com | password123 |
| janedoe | jane@example.com | password123 |
| bobsmith | bob@example.com | password123 |

Seed data includes: 3 users, 5 articles, 7 tags, 5 comments, 6 favorites, and 4 follow relationships.

---

## Authentication & Security

### JWT Workflow

1. **Register** (`POST /users`) or **Login** (`POST /users/login`) to receive a JWT token.
2. Include the token in subsequent requests via the `Authorization` header:
   ```
   Authorization: Token <jwt-token>
   ```
3. The `JwtTokenFilter` intercepts every request, extracts the token, validates it, and populates the Spring Security context with the authenticated `User`.

### Configuration

| Property | Description | Default |
|---|---|---|
| `jwt.secret` | HMAC-SHA512 signing key | (set in `application.properties`) |
| `jwt.sessionTime` | Token expiry in seconds | `86400` (24 hours) |

### Security Rules

The security configuration (`WebSecurityConfig`) enforces:

| Endpoint Pattern | Access |
|---|---|
| `OPTIONS /**` | Permit all (CORS preflight) |
| `POST /users`, `POST /users/login` | Permit all (registration & login) |
| `GET /articles/**`, `GET /profiles/**`, `GET /tags` | Permit all (public reads) |
| `GET /articles/feed` | Authenticated only |
| `GET/POST /graphql`, `/graphiql` | Permit all |
| All other endpoints | Authenticated only |

**Additional details:**
- CSRF is disabled (stateless API).
- Sessions are `STATELESS` — no server-side session storage.
- CORS allows all origins with `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD` methods.
- Passwords are hashed with **BCrypt**.

---

## REST API Reference

Base URL: `http://localhost:8080`

### Authentication

#### Register

```
POST /users
Content-Type: application/json

{
  "user": {
    "username": "jacob",
    "email": "jake@example.com",
    "password": "jakejake"
  }
}
```

**Response** `201 Created`:
```json
{
  "user": {
    "email": "jake@example.com",
    "token": "jwt.token.here",
    "username": "jacob",
    "bio": null,
    "image": null
  }
}
```

#### Login

```
POST /users/login
Content-Type: application/json

{
  "user": {
    "email": "jake@example.com",
    "password": "jakejake"
  }
}
```

**Response** `200 OK`: Same shape as registration.

### Current User

#### Get Current User

```
GET /user
Authorization: Token <jwt>
```

#### Update Current User

```
PUT /user
Authorization: Token <jwt>
Content-Type: application/json

{
  "user": {
    "email": "jake@example.com",
    "bio": "I like to code",
    "image": "https://example.com/avatar.png"
  }
}
```

### Profiles

#### Get Profile

```
GET /profiles/{username}
```

**Response** `200 OK`:
```json
{
  "profile": {
    "username": "jake",
    "bio": "I like to code",
    "image": "https://example.com/avatar.png",
    "following": false
  }
}
```

#### Follow User

```
POST /profiles/{username}/follow
Authorization: Token <jwt>
```

#### Unfollow User

```
DELETE /profiles/{username}/follow
Authorization: Token <jwt>
```

### Articles

#### List Articles

```
GET /articles?tag=spring-boot&author=johndoe&favorited=janedoe&limit=20&offset=0
```

Query parameters (all optional):

| Param | Description |
|---|---|
| `tag` | Filter by tag name |
| `author` | Filter by author username |
| `favorited` | Filter by user who favorited |
| `limit` | Number of results (default: 20) |
| `offset` | Pagination offset (default: 0) |

#### Feed Articles

```
GET /articles/feed?limit=20&offset=0
Authorization: Token <jwt>
```

Returns articles from users the authenticated user follows.

#### Get Article

```
GET /articles/{slug}
```

#### Create Article

```
POST /articles
Authorization: Token <jwt>
Content-Type: application/json

{
  "article": {
    "title": "How to train your dragon",
    "description": "Ever wonder how?",
    "body": "It takes a Jacobian",
    "tagList": ["dragons", "training"]
  }
}
```

#### Update Article

```
PUT /articles/{slug}
Authorization: Token <jwt>
Content-Type: application/json

{
  "article": {
    "title": "Updated title"
  }
}
```

Only the article author can update it.

#### Delete Article

```
DELETE /articles/{slug}
Authorization: Token <jwt>
```

Only the article author can delete it.

#### Favorite Article

```
POST /articles/{slug}/favorite
Authorization: Token <jwt>
```

#### Unfavorite Article

```
DELETE /articles/{slug}/favorite
Authorization: Token <jwt>
```

### Comments

#### Get Comments

```
GET /articles/{slug}/comments
```

#### Add Comment

```
POST /articles/{slug}/comments
Authorization: Token <jwt>
Content-Type: application/json

{
  "comment": {
    "body": "Great article!"
  }
}
```

#### Delete Comment

```
DELETE /articles/{slug}/comments/{id}
Authorization: Token <jwt>
```

The comment author or the article author can delete a comment.

### Tags

#### Get Tags

```
GET /tags
```

**Response** `200 OK`:
```json
{
  "tags": ["java", "spring-boot", "web-development", "tutorial"]
}
```

---

## GraphQL API Reference

**Endpoint**: `POST /graphql`

The GraphQL schema is defined in `src/main/resources/schema/schema.graphqls` and is powered by the [Netflix DGS Framework](https://netflix.github.io/dgs/).

### Queries

| Query | Description | Auth Required |
|---|---|---|
| `article(slug: String!)` | Fetch a single article by slug | No |
| `articles(first, after, last, before, authoredBy, favoritedBy, withTag)` | Paginated article listing with filters | No |
| `feed(first, after, last, before)` | Feed from followed users | Yes |
| `me` | Current authenticated user | Yes |
| `profile(username: String!)` | Fetch user profile | No |
| `tags` | List all tags | No |

### Mutations

| Mutation | Description | Auth Required |
|---|---|---|
| `createUser(input: CreateUserInput)` | Register new user | No |
| `login(email, password)` | Authenticate user | No |
| `updateUser(changes: UpdateUserInput!)` | Update current user | Yes |
| `followUser(username)` | Follow a user | Yes |
| `unfollowUser(username)` | Unfollow a user | Yes |
| `createArticle(input: CreateArticleInput!)` | Create article | Yes |
| `updateArticle(slug, changes: UpdateArticleInput!)` | Update article | Yes |
| `deleteArticle(slug)` | Delete article | Yes |
| `favoriteArticle(slug)` | Favorite an article | Yes |
| `unfavoriteArticle(slug)` | Unfavorite an article | Yes |
| `addComment(slug, body)` | Add comment to article | Yes |
| `deleteComment(slug, id)` | Delete comment | Yes |

### Pagination (Cursor-based)

GraphQL queries use **cursor-based pagination** via the Relay connection pattern:

```graphql
query {
  articles(first: 10, after: "cursor-string") {
    edges {
      cursor
      node {
        title
        slug
        author { username }
      }
    }
    pageInfo {
      hasNextPage
      endCursor
    }
  }
}
```

The cursor is a `DateTimeCursor` — a base64-encoded timestamp used for stable pagination.

### Authentication in GraphQL

Pass the JWT token via the `Authorization` HTTP header (same as REST):

```
Authorization: Token <jwt-token>
```

### Example: Create User

```graphql
mutation {
  createUser(input: {
    email: "jake@example.com"
    username: "jacob"
    password: "jakejake"
  }) {
    ... on UserPayload {
      user {
        email
        token
        username
      }
    }
    ... on Error {
      message
      errors { key value }
    }
  }
}
```

### Union Types

`createUser` returns a `UserResult` **union type** that can be either `UserPayload` (success) or `Error` (validation failure). Use inline fragments (`... on`) to handle both cases.

---

## Testing

### Unit & Integration Tests (JUnit 5)

```bash
./gradlew test
```

This runs **68 tests** across multiple layers:

| Test Category | Location | What is tested |
|---|---|---|
| REST API | `src/test/java/io/spring/api/` | Controller endpoints via MockMvc + REST Assured |
| Application Services | `src/test/java/io/spring/application/` | Query service logic |
| Domain Entities | `src/test/java/io/spring/core/` | Entity behavior (e.g., slug generation) |
| Repositories | `src/test/java/io/spring/infrastructure/` | MyBatis mapper correctness against SQLite |

> **Note**: JaCoCo coverage verification threshold is 80% but actual coverage is ~33%. The Gradle task `jacocoTestCoverageVerification` will fail — skip it with:
> ```bash
> ./gradlew test -x jacocoTestCoverageVerification
> ```

### End-to-End Tests (Selenium + TestNG)

```bash
./gradlew seleniumTest
```

Selenium tests are in `src/test/java/io/spring/selenium/` and use TestNG (configured via `src/test/resources/selenium/testng-smoke.xml`). They are excluded from the standard `test` task.

---

## Code Formatting

The project uses [Spotless](https://github.com/diffplug/spotless) with **Google Java Format**.

```bash
# Check formatting
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessJavaApply
```

Spotless is applied to all `*.java` files except those in `build/generated/`.

---

## Docker

Build and run the application as a Docker container:

```bash
# Build the image
./gradlew bootBuildImage --imageName spring-boot-realworld-example-app

# Run the container
docker run -p 8081:8080 spring-boot-realworld-example-app
```

The application will be available at **http://localhost:8081**.

---

## Frontend (Next.js)

The `frontend/` directory contains a **Next.js** (React) single-page application that serves as the Conduit UI.

### Setup

```bash
cd frontend
npm install
npm run dev
```

- **Dev server**: http://localhost:3000
- **Backend API**: http://localhost:8080 (must be running)
- **Recommended Node.js**: v14–16 (specified in `frontend/.nvmrc`)

### Frontend Scripts

| Command | Description |
|---|---|
| `npm run dev` | Start development server with hot reload |
| `npm run build` | Create production build |
| `npm run start` | Start production server |

---

## CI/CD

The project uses **GitHub Actions** (`.github/workflows/gradle.yml`):

- **Trigger**: Push or pull request to any branch.
- **Environment**: Ubuntu with Zulu JDK 11.
- **Steps**: Checkout → Setup Java 11 → Cache Gradle → `./gradlew clean test -x jacocoTestCoverageVerification`

---

## Configuration Reference

### `application.properties`

| Property | Description | Default |
|---|---|---|
| `spring.datasource.url` | JDBC connection URL | `jdbc:sqlite:dev.db` |
| `spring.datasource.driver-class-name` | JDBC driver | `org.sqlite.JDBC` |
| `spring.jackson.deserialization.UNWRAP_ROOT_VALUE` | Require JSON root wrappers (`"user": {...}`) | `true` |
| `image.default` | Default user avatar URL | `https://static.productionready.io/images/smiley-cyrus.jpg` |
| `jwt.secret` | JWT signing secret (HMAC-SHA512) | (set in file) |
| `jwt.sessionTime` | Token TTL in seconds | `86400` |
| `mybatis.configuration.cache-enabled` | Enable MyBatis caching | `true` |
| `mybatis.configuration.map-underscore-to-camel-case` | Map `snake_case` columns to `camelCase` fields | `true` |
| `mybatis.mapper-locations` | Location of XML mapper files | `mapper/*.xml` |

### Key Gradle Tasks

| Task | Description |
|---|---|
| `./gradlew bootRun` | Run the application (deletes and recreates `dev.db`) |
| `./gradlew test` | Run unit and integration tests |
| `./gradlew seleniumTest` | Run Selenium E2E tests |
| `./gradlew spotlessJavaApply` | Auto-format Java code |
| `./gradlew spotlessCheck` | Check code formatting |
| `./gradlew clean` | Clean build outputs and delete `dev.db` |
| `./gradlew bootBuildImage` | Build Docker image via Cloud Native Buildpacks |
| `./gradlew generateJava` | Generate Java types from the GraphQL schema |
| `./gradlew jacocoTestReport` | Generate test coverage report (HTML + XML) |
| `./gradlew dependencies` | List project dependencies |
