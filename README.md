# SaaS Authentication Platform (Auth0-lite)
A production-style authentication platform built with **Spring Boot** that allows developers to manage authentication for their applications.

Inspired by platforms like Auth0 and Firebase, this project demonstrates how modern authentication systems are designed using **JWT, stateless security, and role-based access control**.
---

## Overview
This platform enables developers to:
* Create accounts
* Authenticate securely
* Generate and use JWT tokens
* Create and manage applications
* Secure APIs using role-based access
---

## Core Features
### Authentication
* Developer signup & login
* Secure password hashing using **BCrypt**
* JWT-based authentication (stateless)

### Security Architecture
* Custom JWT authentication filter
* Stateless session management
* Token validation and signature verification
* Secure API endpoints using Spring Security

### Authorization (RBAC)
* Role-Based Access Control
* Roles:
  * `ROLE_DEVELOPER`
  * `ROLE_ADMIN`
* Endpoint protection using roles

### Application Management
* Developers can create applications
* Ownership enforced via JWT (no client-side ID passing)
* Secure multi-tenant-ready design
---

## System Architecture
```text
Client (Postman / Frontend)
        │
        ▼
Security Filter (JWT)
        │
        ▼
Controller Layer
        │
        ▼
Service Layer (Business Logic)
        │
        ▼
Repository Layer (JPA)
        │
        ▼
Database (MySQL)
```
---

## Authentication Flow
1. Developer signs up
2. Developer logs in → receives JWT
3. Client stores token
4. Requests include:
```http
Authorization: Bearer <token>
```
5. JWT Filter intercepts request
6. Token is validated
7. Developer identity is extracted
8. Request proceeds if authorized
---

## Security Design

* Stateless authentication (no server sessions)
* JWT signed using a secure secret key
* Passwords hashed with BCrypt
* Role-based endpoint authorization
* Identity extracted from JWT (not request parameters)
---

## API Endpoints
### Public Endpoints
```http
POST /developers/signup
POST /developers/login
```
---

### Protected Endpoints
```http
POST /applications
Authorization: Bearer <token>
```
---

## Example Requests
### Signup
```json
POST /developers/signup
{
  "email": "test@gmail.com",
  "password": "123456"
}
```
---

### Login
```json
POST /developers/login
{
  "email": "test@gmail.com",
  "password": "123456"
}
```
---

### Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
---

### Create Application
```http
POST /applications
Authorization: Bearer <token>
```
```json
{
  "name": "FarmSA",
  "description": "Agri platform"
}
```
---

## Tech Stack
* **Backend:** Java, Spring Boot
* **Security:** Spring Security, JWT (jjwt)
* **Database:** MySQL
* **ORM:** Spring Data JPA
* **Build Tool:** Maven
* **Utilities:** Lombok
---

## Project Structure
```text
auth-platform/
├── config/
├── developer/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── application/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── security/
│   ├── jwt/
│   └── filter/
```
---

## Key Concepts Implemented
* JWT authentication flow
* Stateless backend architecture
* Custom authentication filters
* SecurityContext handling
* Role-based authorization (RBAC)
* Clean layered architecture (Controller → Service → Repository)
---

## What I Learned
* How real authentication systems work internally
* Designing secure, scalable APIs
* Implementing JWT from scratch
* Spring Security deep internals
* Protecting endpoints using roles
* Debugging authentication & authorization issues
---

## Future Improvements
* OAuth login (Google, Microsoft)
* API key management system
* Multi-tenant user authentication (Firebase-style)
* Refresh tokens & token rotation
* Rate limiting & monitoring
* Admin dashboard
---

## Project Status
```text
JWT Authentication
RBAC Authorization
Multi-tenant user authentication (next phase)
```
---

## Author
**Siphamandla Ngcepe**
---

## Final Note
This project demonstrates real-world backend engineering principles used in modern authentication platforms. It is designed to reflect **production-level architecture and security practices**, not just basic CRUD functionality.
