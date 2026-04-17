# 🏦 Digital Banking Platform

Enterprise-style digital banking backend built with **Java 17** and **Spring Boot**.

The system provides secure authentication using **JWT access & refresh tokens**, role-based authorization, account management, and transaction processing, together with production-style security integration tests.

🔗 Repository: https://github.com/petaryankov/digital-banking-platform

---

## ✨ Features

### 🔐 Authentication & Security
- JWT Access Tokens
- JWT Refresh Tokens stored in database
- Token expiration & revocation
- Role-based access control (USER / ADMIN)
- Stateless REST API
- Protected frontend routes (guards)

### 🏦 Banking Capabilities
- Account creation (multi-currency: EUR, USD)
- Deposit / Withdraw operations
- Secure transfers between accounts
- Ownership validation(JWT-based)
- Transaction history

### 🖥 Frontend Features (React)
- Modern dashboard UI (TailwindCSS)
- Authentication context (global state)
- Route guards:
  - AuthGuard
  - AdminGuard
  - PublicGuard
- Account management UI
- Deposit / Withdraw modals
- Admin dashboard:
  - Activate / deactivate users
- Real-time UI updates after transactions

### 🧪 Testing & Quality
- Integration security tests with MockMvc
- SQL fixtures for deterministic test runs
- Global exception handling
- Layered architecture (controller → service → repository)

---

## 🧰 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- MySQL

### Frontend
- React 19
- React Router
- Context API
- Axios
- TailwindCSS
### Testing
- JUnit 5
- MockMvc
- SQL scripts

### 🏗 Architecture Overview

Frontend (React)
   ↓
REST API (Spring Boot)
   ↓
Service Layer (Business Logic)
   ↓
Repository Layer (JPA)
   ↓
MySQL Database
---

## 🚀 Getting Started

### Clone repository

```bash
git clone https://github.com/petaryankov/digital-banking-platform.git
cd digital-banking-platform/backend
```

---

## ⚙️ Environment Variables

The application uses environment variables for credentials and secrets.

```bash
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_super_secret_key
```

---

## 🧩 Example Configuration (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/digital_banking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000
  refresh-expiration: 604800000

server:
  port: 8080
```

---

## ▶️ Run the Application

### Linux / macOS

```bash
export DB_USERNAME=root
export DB_PASSWORD=root
export JWT_SECRET=verySecretKey
./mvnw spring-boot:run
```

### Windows (PowerShell)

```powershell
setx DB_USERNAME root
setx DB_PASSWORD root
setx JWT_SECRET verySecretKey
./mvnw spring-boot:run
```

Application will start at:

```
http://localhost:8080
```

---

## 📘 Swagger API Documentation

After startup:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Authentication Flow

### Login
```
POST /api/auth/login
```

Response:
```json
{
  "accessToken": "JWT_ACCESS_TOKEN",
  "refreshToken": "JWT_REFRESH_TOKEN"
}
```

---

### Access secured endpoints

```
Authorization: Bearer <access_token>
```

---

### Refresh access token
```
POST /api/auth/refresh
```

Request:
```json
{
  "refreshToken": "<refresh_token>"
}
```

---

## 👥 Default Test Users

| Role  | Email           | Password |
|-------|-----------------|----------|
| USER  | user@test.com   | password |
| ADMIN | admin@test.com  | password |

---

## 🧪 Run Tests

```bash
./mvnw test
```

Security tests validate:

- request without token
- malformed token
- access token used as refresh
- revoked token
- expired token
- successful refresh

---

## 🏛 Project Structure

```
controller   → REST endpoints
service      → business logic
repository   → database layer
security     → JWT & filters
dto          → request / response objects
exception    → global error handling
```

---

## 🔒 Security Design

- Stateless authentication
- Signed JWT verification
- Refresh token persistence
- Revocation & expiration validation
- Role-based authorization

---

## 📄 Error Response Format

```json
{
  "timestamp": "2026-02-12T12:34:56",
  "status": 403,
  "error": "Forbidden",
  "message": "Refresh token not found"
}
```

---

## 👨‍💻 Author

Petar Yankov  
https://github.com/petaryankov

---

## 🚀 Next Phase

Frontend application (React) & containerization.
