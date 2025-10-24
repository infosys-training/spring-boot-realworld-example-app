# Keycloak OAuth2 Integration Setup Guide

This document provides instructions for setting up and testing the Keycloak OAuth2 authentication integration alongside the existing JWT authentication in the Spring Boot RealWorld example application.

## Overview

The application now supports **dual authentication** - both existing custom JWT tokens and Keycloak OAuth2 tokens work simultaneously. This allows for gradual migration from the custom JWT implementation to Keycloak without breaking existing clients.

## Architecture

- **Custom JWT Authentication**: Existing implementation using `JwtTokenFilter` and `DefaultJwtService`
  - Token format: `Authorization: Token {jwt_token}`
  - Used by `/users/login` endpoint
  - Continues to work without any changes

- **Keycloak OAuth2 Authentication**: New implementation using Spring Security OAuth2 Resource Server
  - Token format: `Authorization: Bearer {access_token}`
  - Tokens issued by Keycloak
  - Maps Keycloak user claims to existing User entities

## Starting Keycloak

### 1. Start Keycloak using Docker Compose

```bash
cd ~/repos/spring-boot-realworld-example-app
docker-compose up -d keycloak
```

Keycloak will be available at http://localhost:8180

**Default Credentials**: The docker-compose.yml uses default admin credentials (username: `admin`, password: `admin`) for local development. To use different credentials, set environment variables before starting:

```bash
export KEYCLOAK_ADMIN=your_username
export KEYCLOAK_ADMIN_PASSWORD=your_password
docker-compose up -d keycloak
```

**Security Note**: For production deployments, always use strong credentials and manage them securely (e.g., secrets management service).

### 2. Access Keycloak Admin Console

- URL: http://localhost:8180
- Default Username: `admin`
- Default Password: `admin`

(These can be overridden via `KEYCLOAK_ADMIN` and `KEYCLOAK_ADMIN_PASSWORD` environment variables)

## Configure Keycloak Realm and Client

### 1. Create Realm

1. In the Keycloak Admin Console, click **"Create Realm"** (or select realm dropdown)
2. Name: `spring-boot-realworld`
3. Click **"Create"**

### 2. Create Client

1. Go to **"Clients"** → **"Create client"**
2. Configure the client:
   - **Client ID**: `realworld-app`
   - **Client authentication**: OFF (public client)
   - **Standard flow**: Enabled
   - **Direct access grants**: Enabled (for password grant testing)
3. Click **"Next"**, then **"Save"**
4. In the client settings:
   - **Valid redirect URIs**: `http://localhost:8080/*`
   - **Web origins**: `http://localhost:8080`
   - Click **"Save"**

### 3. Create Test Users

Create users in Keycloak that match the existing database users:

#### User 1: John Doe
1. Go to **"Users"** → **"Add user"**
2. Configure:
   - **Username**: `johndoe`
   - **Email**: `john@example.com`
   - **Email verified**: ON
3. Click **"Create"**
4. Go to **"Credentials"** tab
5. Click **"Set password"**:
   - **Password**: `password123`
   - **Temporary**: OFF
6. Click **"Set password"**

#### User 2: Jane Doe
Repeat the above steps with:
- **Username**: `janedoe`
- **Email**: `jane@example.com`
- **Password**: `password123`

#### User 3: Bob Smith
Repeat the above steps with:
- **Username**: `bobsmith`
- **Email**: `bob@example.com`
- **Password**: `password123`

## Testing Authentication

### Prerequisites

1. Keycloak is running and configured
2. Spring Boot application is running: `./gradlew bootRun`
3. Users are created in both Keycloak and the application database

### Test 1: Custom JWT Authentication (Existing)

This method continues to work without any changes.

#### Login and get token:
```bash
curl -X POST 'http://localhost:8080/users/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "user": {
      "email": "john@example.com",
      "password": "password123"
    }
  }'
```

Response:
```json
{
  "user": {
    "email": "john@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "johndoe",
    "bio": "",
    "image": ""
  }
}
```

#### Use token to access protected endpoint:
```bash
curl -X GET 'http://localhost:8080/user' \
  -H 'Authorization: Token eyJhbGciOiJIUzI1NiJ9...'
```

### Test 2: Keycloak OAuth2 Authentication (New)

#### Get Keycloak access token:
```bash
curl -X POST 'http://localhost:8180/realms/spring-boot-realworld/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=realworld-app' \
  -d 'username=johndoe' \
  -d 'password=password123'
```

Response:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

#### Use Keycloak token to access protected endpoint:
```bash
curl -X GET 'http://localhost:8080/user' \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...'
```

### Test 3: Verify Both Methods Work for Same Endpoints

Both authentication methods should work for all protected endpoints:

```bash
# Get articles feed (requires authentication)
# Using custom JWT:
curl -X GET 'http://localhost:8080/articles/feed' \
  -H 'Authorization: Token {custom_jwt_token}'

# Using Keycloak token:
curl -X GET 'http://localhost:8080/articles/feed' \
  -H 'Authorization: Bearer {keycloak_access_token}'

# Create article (requires authentication)
# Using custom JWT:
curl -X POST 'http://localhost:8080/articles' \
  -H 'Authorization: Token {custom_jwt_token}' \
  -H 'Content-Type: application/json' \
  -d '{
    "article": {
      "title": "Test Article",
      "description": "Test Description",
      "body": "Test Body",
      "tagList": ["test"]
    }
  }'

# Using Keycloak token:
curl -X POST 'http://localhost:8080/articles' \
  -H 'Authorization: Bearer {keycloak_access_token}' \
  -H 'Content-Type: application/json' \
  -d '{
    "article": {
      "title": "Test Article 2",
      "description": "Test Description",
      "body": "Test Body",
      "tagList": ["test"]
    }
  }'
```

## User Mapping

The Keycloak JWT tokens are mapped to existing User entities through the `KeycloakJwtAuthenticationConverter`. The converter:

1. Extracts the `email` claim from the Keycloak JWT
2. Falls back to `preferred_username` if email is not present
3. Looks up the user in the local database by email or username
4. Returns an authentication token with the User entity as the principal

**Important**: Users must exist in both Keycloak and the local database for authentication to work. The user is identified by matching the email or username.

## Troubleshooting

### Issue: 401 Unauthorized with Keycloak token

**Causes**:
- Keycloak realm or client not configured correctly
- User doesn't exist in local database
- Token expired
- Wrong issuer URI in application.properties

**Solutions**:
- Verify Keycloak realm is named `spring-boot-realworld`
- Verify client ID is `realworld-app`
- Check user exists in database with matching email/username
- Verify token hasn't expired (default: 5 minutes)
- Check `spring.security.oauth2.resourceserver.jwt.issuer-uri` in application.properties

### Issue: Keycloak not accessible

**Cause**: Keycloak container not running

**Solution**:
```bash
docker-compose up -d keycloak
# Wait 30 seconds for Keycloak to start
sleep 30
# Check Keycloak is running
curl http://localhost:8180/health/ready
```

### Issue: User not found

**Cause**: User exists in Keycloak but not in local database

**Solution**: Users must be created in both systems. Use the `/users` registration endpoint to create users in the database:

```bash
curl -X POST 'http://localhost:8080/users' \
  -H 'Content-Type: application/json' \
  -d '{
    "user": {
      "username": "johndoe",
      "email": "john@example.com",
      "password": "password123"
    }
  }'
```

## Configuration Reference

### Application Properties

The following properties are configured in `src/main/resources/application.properties`:

```properties
# Keycloak OAuth2 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/spring-boot-realworld
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/spring-boot-realworld/protocol/openid-connect/certs
```

### Docker Compose Configuration

Keycloak service configuration in `docker-compose.yml`:

```yaml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:23.0
    container_name: keycloak
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8180:8080"
    command:
      - start-dev
```

## Migration Strategy

For gradual migration from custom JWT to Keycloak:

1. **Phase 1**: Deploy dual authentication (current state)
   - Both authentication methods work
   - Existing clients continue using custom JWT
   - New clients can use Keycloak

2. **Phase 2**: Migrate clients to Keycloak
   - Update frontend to use Keycloak for authentication
   - Migrate mobile apps to use Keycloak
   - Monitor usage of custom JWT endpoints

3. **Phase 3**: Deprecate custom JWT
   - Add deprecation warnings to custom JWT endpoints
   - Set sunset date for custom JWT support
   - Communicate migration timeline to users

4. **Phase 4**: Remove custom JWT (future)
   - Remove `JwtTokenFilter` and related code
   - Keep only Keycloak OAuth2 authentication

## Security Considerations

- **Token Storage**: Access tokens should be stored securely (e.g., httpOnly cookies for web apps)
- **Token Expiration**: Keycloak tokens expire after 5 minutes by default. Use refresh tokens for longer sessions.
- **HTTPS**: Use HTTPS in production for both the application and Keycloak
- **Secrets**: Change default Keycloak admin credentials in production
- **CORS**: Configure CORS properly for frontend integration

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring Boot OAuth2 Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.security.oauth2)
