# Blog Platform

Spring Fundamentals Regular Exam project — a blog-like web application with posts, categories, comments, and role-based access control.

## Tech Stack

- Java 17+
- Spring Boot 3.4.0
- Maven
- Spring MVC + Thymeleaf
- Spring Data JPA
- Spring Security (session-based login)
- MySQL

## Features

### Domain entities

- **Post** — blog articles with title, content, author, and category
- **Category** — groups posts by topic
- **Comment** — reader feedback on posts

Technical entities: `User`, `Role` (not counted toward the 3 domain entities).

### Valid domain functionalities

1. **Create post** — authors publish a new article (POST)
2. **Edit post** — authors update their posts; admins can edit any post (POST)
3. **Add comment** — logged-in users comment on a post (POST)
4. **Delete comment** — comment owner or admin removes a comment (POST)

### Full CRUD

`Post` supports create, read, update, and delete from the UI.

### Security

- Session-based login stores `user_id` in the HTTP session
- Passwords are hashed with BCrypt
- **Guest:** register, login, browse public pages
- **READER:** comment on posts
- **AUTHOR:** create/edit/delete own posts
- **ADMIN:** manage categories, delete any post/comment

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.9+
- MySQL 8+

### Database setup

1. Start MySQL.
2. Update credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

The app creates the `blog_platform` database automatically.

### Run the application

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

### Seeded accounts

| Username | Password   | Role   |
|----------|------------|--------|
| admin    | admin123   | ADMIN  |
| author   | author123  | AUTHOR |

New registrations receive the **READER** role.

## Project Structure

```
src/main/java/com/blogplatform/
  config/         Security, data seeding, web config
  model/entity/   JPA entities (UUID primary keys)
  repository/     Spring Data JPA repositories
  service/        Business logic
  web/controller/ MVC controllers
  web/dto/        Form DTOs with validation
  exception/      Custom runtime exceptions
```

## Exam Notes

- Public Git repository required for submission
- Use at least 10 conventional commits across 3 different days before the deadline
- Do not push new commits after submission until evaluation completes
