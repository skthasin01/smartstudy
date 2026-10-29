# Smart Study Load and Burnout Prevention System

A production-ready academic workload scheduling and early-warning burnout prevention application built with **Java 8**, **Spring Boot 2.7.18**, **Spring Data JPA**, **Spring Security (JWT)**, **PostgreSQL / H2**, and a responsive **React (Vite + Bootstrap 5)** frontend.

Designed for seamless import, debugging, and demonstration in **Apache NetBeans IDE**.

---

## 1. System Overview

Students frequently encounter academic fatigue, deadline compression, and uneven study distribution. This system provides:
1. **Student & Course Management:** Organize coursework with color-coded subjects.
2. **Study Task Management:** Complete CRUD, dynamic filtering by status, difficulty, priority, and date range, with overdue warnings.
3. **Rule-Based Automatic Study Plan Generator:** Distributes pending tasks across available days into manageable focus blocks (e.g. 50-minute Pomodoro intervals) without exceeding daily study capacity. Transparently explains all scheduling decisions.
4. **Live Study Session Timer:** Real-time server-calculated stopwatch with break logging and task completion synchronization.
5. **Daily Wellness Check-in:** Optional tracking of stress (1–5), energy (1–5), mood states, and sleep hours.
6. **Deterministic Workload Index:** Computes 0–100 workload scores based on task volume, difficulty multipliers, deadline proximity, and overdue penalties.
7. **Early-Warning Burnout Risk Assessment:** Multi-factor scoring assessing academic load, session strain, and optional wellness indicators.
8. **Personalized Supportive Recommendations:** Actionable academic guidance without making medical claims.
9. **In-App Notifications:** Scheduled hourly alerts for upcoming deadlines, missed tasks, and excessive strain.
10. **GDPR / Privacy Controls:** Student-governed wellness tracking toggle and one-click data erasure.

---

## 2. Technology Stack

- **Backend:** Java 8 (1.8 source and target compatibility)
- **Framework:** Spring Boot 2.7.18 (final stable release)
- **Security:** Spring Security with BCrypt password hashing & JJWT 0.11.5 (HMAC-SHA256)
- **Persistence:** Spring Data JPA with Hibernate 5.6
- **Database:** PostgreSQL (Primary Production) & H2 (Instant Offline Dev / Testing)
- **Validation:** Bean Validation using `javax.validation` (NOT `jakarta.*`)
- **API Documentation:** `springdoc-openapi-ui` 1.8.0 (OpenAPI 3 / Swagger UI)
- **Testing:** JUnit 5 & Mockito
- **Frontend:** React 18, Vite, Bootstrap 5, and Bootstrap Icons

---

## 3. Apache NetBeans IDE Setup Guide

### Opening the Project in NetBeans
1. Launch **Apache NetBeans IDE** (NetBeans 8.2, 11+, 12+, or later).
2. Click **File** -> **Open Project...**
3. Navigate to `c:\cse\MyProjects\Study_Project` and select it (NetBeans will display the Maven project icon).
4. Click **Open Project**.
5. NetBeans will automatically detect `pom.xml` and resolve all dependencies from Maven Central.

### Running from NetBeans
- **Run with H2 (Zero-Setup Instant Mode):**
  Right-click project in NetBeans -> **Run** (or press `F6`). To specify the H2 profile, open Project Properties -> **Actions** -> **Run project** -> Set VM Options:
  ```
  -Dspring.profiles.active=h2
  ```
- **Run with PostgreSQL:**
  Ensure PostgreSQL is running, create the database, and run normally.

---

## 4. Database Setup (PostgreSQL)

### 1. Create the Database
In PostgreSQL (`psql` or pgAdmin):
```sql
CREATE DATABASE smart_study_db;
```

### 2. Environment Variables Configuration
Set the following environment variables (or update `src/main/resources/application.properties`):
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smart_study_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password_here
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### 3. Out-of-the-Box H2 Database Profile
For demonstration or offline development without PostgreSQL:
```bash
# Using the Maven wrapper
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2

# Or running the packaged JAR
java -jar target\smart-study-load-system-1.0.0.jar --spring.profiles.active=h2
```
H2 Console is available at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/smart_study_h2`
- User: `sa`
- Password: (blank)

---

## 5. API Documentation (Swagger / OpenAPI)

Once the backend is running, access:
- **Interactive Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI v3 JSON Specification:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Core REST Endpoints:
| Domain | Method | Endpoint | Description |
|---|---|---|---|
| **Auth** | POST | `/api/auth/register` | Register new student account |
| **Auth** | POST | `/api/auth/login` | Log in and receive JWT token |
| **Users** | GET | `/api/users/me` | Current user profile |
| **Users** | PUT | `/api/users/profile` | Update profile information |
| **Users** | PUT | `/api/users/password` | Change password |
| **Courses** | GET/POST | `/api/courses` | List / Create student courses |
| **Courses** | PUT/DELETE | `/api/courses/{id}` | Update / Delete course |
| **Tasks** | GET/POST | `/api/tasks` | Filtered list / Create study task |
| **Tasks** | PUT/PATCH/DELETE | `/api/tasks/{id}` | Update / Status toggle / Delete task |
| **Study Plans** | POST | `/api/study-plans/generate` | Generate rule-based study schedule |
| **Study Plans** | GET | `/api/study-plans/current` | Active plan & explanation breakdown |
| **Study Sessions** | POST | `/api/study-sessions/start` | Start live focus session |
| **Study Sessions** | POST | `/api/study-sessions/end` | Conclude session & record breaks |
| **Daily Check-ins** | POST | `/api/check-ins` | Submit daily wellness reflection |
| **Daily Check-ins** | DELETE | `/api/check-ins/my-data` | GDPR erase all wellness logs |
| **Workload** | GET | `/api/workload/current` | Calculate & view workload index |
| **Workload** | GET | `/api/workload/trends` | Historical workload trend data |
| **Burnout** | GET | `/api/burnout/current` | Early-warning burnout risk score |
| **Recommendations** | GET | `/api/recommendations` | View personalized study recommendations |
| **Notifications** | GET | `/api/notifications` | View in-app notifications |
| **Analytics** | GET | `/api/analytics/dashboard` | Complete aggregated dashboard payload |
| **Settings** | GET/PUT | `/api/settings` | Student study goals & privacy controls |

---

## 6. Running the Frontend (React + Vite)

The frontend connects directly to the backend at `http://localhost:8080/api`:

```bash
cd frontend
npm install
npm run dev
```
Open your browser at: `http://localhost:5173`

A **"Quick Fill Demo Account"** button is provided on the login page for instant one-click demonstration.

---

## 7. Automated Testing & Verification

Run the full test suite via Maven:
```bash
.\mvnw.cmd test
```
All 20 unit and integration tests verify:
- BCrypt password hashing & JWT generation
- Course and task student ownership validation
- Rule-based study plan block generation & daily cap enforcement
- Workload scoring with difficulty & deadline multipliers
- Burnout risk assessment with and without wellness data
- Active session conflict prevention and elapsed duration calculation
- Notification throttling & deduplication

---

## 8. Production Migration Strategy

For production deployment:
1. **Schema Migration:** Integrate Flyway or Liquibase (`flyway-core` dependency) to manage schema versions sequentially instead of `hibernate.ddl-auto=update`.
2. **Reverse Proxy:** Deploy behind Nginx or AWS Application Load Balancer with HTTPS (TLS 1.3).
3. **Secrets Management:** Use HashiCorp Vault, AWS Secrets Manager, or Kubernetes Secrets to inject `SPRING_DATASOURCE_PASSWORD` and `JWT_SECRET`.
