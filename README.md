<p align="center"> 
    <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" /> 
    <img src="https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" /> 
    <img src="https://img.shields.io/badge/MySQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" /> 
    <img src="https://img.shields.io/badge/Swagger-API_Docs-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" /> 
</p>

# Daily Focus - REST API (Spring Boot 4)

Daily Focus is a REST API for managing daily plans and tasks.\
Each user has one **DailyPlan** per day, containing multiple **Tasks**.\
The API handles authentication, plan lifecycle, task management, and admin operations.

---

## Features

- **Authentication, Authorization & Profile** 
  - Register, login, JWT-based auth 
  - View and update personal profile
  - Change password
- **Daily Plans** 
    - Auto-create today’s plan on first access 
    -  Retrieve today’s or past plans 
- - **Tasks** 
  - Add, update, toggle completion, delete
- - **Admin Tools** 
  - List users, deactivate/reactivate accounts 
  - Reset user passwords
  - System stats (users, plans, tasks) 
- - **API Documentation** 
  - Swagger UI available at `/swagger-ui.html`

---

## Tech Stack:
- Java 17
- Spring Boot 4
- Spring Security (JWT)
- Spring Data JPA
- MySQL
- Swagger / Springdoc OpenAPI

---

## Running the Application

### 1. Clone the repository
```bash
https://github.com/nick404s/dailyfocus.git

cd dailyfocus
```
### 2. Configure environment variables

Create application.properties or use env vars:

```code
# --- DB Setup ---
# connection to a local db instance
spring.datasource.url=jdbc:mysql://localhost:3307/focusdb?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
# set user name and password
spring.datasource.username=root
spring.datasource.password=yourpassword
# automatically update the table based on the entities. no table drops
spring.jpa.hibernate.ddl-auto=update
# close the jpa entity manager when transaction ends for efficiency
spring.jpa.open-in-view=false

# --- Swagger URL Setup ---
springdoc.swagger-ui.path=/docs

# --- JWT ---
# you can use a randomly generated key with: openssl rand -hex 32
spring.jwt.secret=your-secret-key
# you can set your token expiration time: 900000 = 15 minutes
sping.jwt.expiration=900000
```

### 3. Start the server
```bash
  ./mvnw spring-boot:run
```

### 4. Swagger / OpenAPI docs
```bash
    http://localhost:8080/swagger-ui/index.html
```

## The Swagger Pics

<img width="1772" height="203" alt="Screenshot 2026-02-24 at 1 13 10 PM" src="https://github.com/user-attachments/assets/2225b0b8-9c8f-4644-bf6a-a9280a4f80c4" />
<img width="1765" height="1224" alt="Screenshot 2026-02-24 at 1 09 43 PM" src="https://github.com/user-attachments/assets/efe97f08-6558-40ff-be50-d93792a8728c" />

