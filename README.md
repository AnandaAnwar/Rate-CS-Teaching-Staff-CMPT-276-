# Rate CS Teaching Staff

A CRUD web app for rating CS teaching staff, built for CMPT 276 Assignment 2.
Stack: Java 21, Spring Boot 4, Thymeleaf, Spring Data JPA (Hibernate) and PostgreSQL.

This is "iteration 1": ratings can be viewed, added, edited and deleted. Features such as
per-person average ratings are not included yet (see Known Issues / Future Work in the submission file).

## Features

- **Index page** (`/`): all ratings with name, role and an overall score (the average of clarity, niceness and knowledgeable score). Each name links to its detail page.
- **Detail page** (`/ratings/{id}`): every attribute, with Edit and Delete.
- **Create page** (`/ratings/new`) and **Edit page** (`/ratings/{id}/edit`): forms with field-specific error messages. Typed values are kept when validation fails.
- **Delete**: a confirmation page is shown before anything is removed.
- **Role-based behaviour (polymorphism):** `StaffMemberProfile` is an abstract class with `displayTitle()` and `roleDescription()`.
  `TaProfile`, `ProfProfile`, `InstructorProfile` and `StaffProfile` implement it. The index and detail pages call these
  methods, so a TA is shown as "Ada (TA)" and a professor as "Prof. Ada".

## Validation rules (server-side)

| Field | Rule |
|---|---|
| name | required, at most 100 characters |
| email | required, must look like name@domain.tld |
| roleType | required: TA, PROF, INSTRUCTOR or STAFF |
| clarity, niceness, knowledgeableScore | required whole numbers from 1 to 10 |
| comment | optional, at most 500 characters |

**Email uniqueness rule:** emails are *not* unique. The email identifies the staff member being rated,
and several students can each rate the same person, so duplicates are allowed on purpose.

## How to run locally

Requirements: Java 21, PostgreSQL and the included Maven wrapper (`mvnw`).

1. Create a PostgreSQL database named `rate_cs_staff`: `CREATE DATABASE rate_cs_staff;` (the app creates the table itself).
2. In PowerShell, from the project folder (the one containing `pom.xml`), set the database password for this terminal session: `$env:DB_PASSWORD = Read-Host "Enter PostgreSQL password" -MaskInput`
   By default the app connects to `jdbc:postgresql://localhost:5433/rate_cs_staff` as user `postgres`.
   To use something different, also set `DB_URL` (a JDBC URL) and `DB_USERNAME`.
3. Start the app: `.\mvnw.cmd spring-boot:run`
4. Open http://localhost:8080

On macOS/Linux use `export DB_PASSWORD=...` and `./mvnw spring-boot:run`.

Optional: `sample-data.sql` inserts 11 made-up ratings (run it once with `psql -U postgres -h localhost -p 5433 -d rate_cs_staff -f sample-data.sql`).

To run the tests (no PostgreSQL needed): `.\mvnw.cmd test`. See TESTING.md.

## How to deploy

The app runs on Render as a Docker web service with a Render PostgreSQL database.

1. **Create the database.** On Render: New > PostgreSQL, free plan.
2. **Copy the connection details** from the database page: hostname, database name, username and password.
3. **Create the web service.** New > Web Service, connect this GitHub repo and choose the **Docker** runtime (it uses the `Dockerfile` in the repo root).
4. **Set these environment variables** on the web service (no secrets are committed to the repo):
   - `DB_URL` = `jdbc:postgresql://<hostname>:5432/<database>`
   - `DB_USERNAME` = the database username
   - `DB_PASSWORD` = the database password

   `DB_URL` must be a JDBC URL starting with `jdbc:postgresql://`. Render provides `PORT` automatically and the app reads it.
5. Deploy and open the `onrender.com` URL. On the free plan the first request after idle time can take about a minute.

## Project layout

- `src/main/java/com/example/ratecsstaff/model/`: StaffRating (entity and validation), RoleType, StaffMemberProfile and its 4 subclasses
- `src/main/java/com/example/ratecsstaff/repository/`: StaffRatingRepository (Spring Data JPA)
- `src/main/java/com/example/ratecsstaff/controller/`: StaffRatingController (all CRUD routes)
- `src/main/resources/templates/`: index, detail, form, delete, fragments
- `src/test/java/...`: validation, controller (MockMvc) and persistence tests