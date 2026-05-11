# Game Shop — Digital Games Catalogue API

A backend REST API for a digital games online store, built with Spring Boot. Visitors can browse and search the game catalogue. Store admins can add, update, and delete games by protected endpoints.

---

## What This Project Does

- Exposes a RESTful catalogue API for digital games
- Visitors can browse all games, search by phrase, filter by genre, developer, age restriction, and release date
- Store admins can create, update, and delete games — protected by HTTP Basic Auth
- Games can have multiple genres (many-to-many relationship)
- GameResponseDTO and GameRequestDTO are used to separate database entities from what user needs
- Genres are pre-seeded on startup using a Spring `CommandLineRunner`
- API documentation available by Swagger UI at `/swagger-ui/index.html`

---

## Tech
- Language: Java 25
- Framework: Spring Boot 4.0.6
- Database: PostgreSQL , H2 (tests)
- Spring Data JPA / Hibernate
- Spring Security — HTTP Basic Auth
- SpringDoc OpenAPI (Swagger UI)
- Build: Gradle
- Containerisation: Docker + Docker Compose
- CI pipeline: GitHub Actions
- Visual validation: Bruno (API endpoints), DBeaver (database)

---

## How to run locally

### Have to have
- Docker Desktop running

### Start the app

```bash
docker compose up --build
```

This starts:
- PostgreSQL on `localhost:5432`
- The product API on `localhost:8081`

### Swagger UI

```
http://localhost:8081/swagger-ui/index.html
```


---

## Environment Variables

To run the app you have to have `.env` file with what is listed below.
Please change the example values to your own:

```
DB_HOST=postgres-db
DB_PORT=5432
DB_NAME=gamestore
DB_USER=gamestore
DB_PASSWORD=gamestore

POSTGRES_DB=gamestore
POSTGRES_USER=gamestore
POSTGRES_PASSWORD=gamestore

ADMIN_USERNAME=admin
ADMIN_PASSWORD=yourpassword
```

---

## API Endpoints

### No auth required - public for all visitors

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/games` | List all games |
| GET | `/api/games/{id}` | Get game by ID |
| GET | `/api/games/search?phrase=` | Search by keyword in title |
| GET | `/api/games/search/exact?title=` | Find by exact title |
| GET | `/api/games/genre/{genre}` | Filter by genre |
| GET | `/api/games/developer/{developer}` | Filter by developer |
| GET | `/api/games/released` | Games already released - based on LocalDate.now() |
| GET | `/api/games/genres?genres=X&genres=Y` | Games with all listed genres |
| GET | `/api/games/age/{restriction}` | Filter by age restriction - PEGI based enum |
| GET | `/api/genres` | List all genres |

### Role based auth required - Admin only 
#### HTTP Basic Auth required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/games` | Add a new game |
| PUT | `/api/games/{id}` | Update a game |
| DELETE | `/api/games/{id}` | Delete a game |

---
## Main Entity
The base entity is Game. It contains:
- title — unique, required
- description — up to 2000 characters
- price — stored as BigDecimal to avoid precision issues
- developer and publisher — required
- ageRestriction — enum: PEGI_3, PEGI_7, PEGI_12, PEGI_16, PEGI_18
- releaseDate — optional
- createdAt and updatedAt — managed automatically
- genres — a set of Genre entities linked by many-to-many join table game_genres
---

## Running Tests

```bash
./gradlew test
```

Tests include:
- **Unit tests** — `GameServiceTest`: service layer tested with Mockito
- **Slice tests** — `GameControllerTest`: web layer tested with `@WebMvcTest`, security rules checked
- **Integration tests** — `GameControllerIntegrationTest`: full Spring context with H2 database - not finished, for now for context loading


## Project Structure
Broadened MVC stucture.
```
src/main/java/com/gameshop/
├── config/          — SecurityConfig, AdminConfig, DataLoader
├── controller/      — GameController, GenreController
├── dto/             — GameRequestDTO, GameResponseDTO
├── model/           — Game, Genre, AgeRestriction (enum)
├── repository/      — GameRepository, GenreRepository
└── service/         — GameService, GenreService
```

---

## What I Learned

### Spring `CommandLineRunner` for data seeding
Instead of relying on a SQL script which I have used before, I used a Spring-managed `DataSeeder` class implementing `CommandLineRunner`. 

### `@RequestParam`
Found a way to bind multiple query parameters into an endopint name to achive a better structured endpoint then normal `@PathVariable`

### `AsserJ`
For easy code reading. I did not know it existed until i stumbled upon it in one of the tutorials I checked for good test structure. I now know how to use.

### `JsonMapper` replaces `ObjectMapper` in Spring Boot 4.x.x
I had to research that after my test build failed. I did not want to use java 21 with Spring boot 3.0.6 simply because I have used it. Now spring initializer recomments Spring boot 4.x.x and Java 25, so I thought I should implement in that setup.

---

## Tools Used for Visual Validation

- Bruno

- DBeaver

---

## AI Usage

AI was not used to blindly generate code, instead I used it to help me with:

- **Error comprehension** —  particularly around Spring Boot 4.x.x changes then confirmed by google search and forums or documentation
- **Research** — finding tutorials and documentation, Helped me faster find Baeldung articles and what to research
- **Architecture discussion** — based on my given structure talking through monorepo vs microservices structure, Maven vs Gradle - normally I would talk with a collaborator, it helped me realize that I was preparing to do a much grander project than I needed eventually helping me ommit the difficult scale down and posiible problems
- **This README structure** 
---

## Possible Future Development
- **Better coverage testing** - now the only things tested are GameService and GameController and the integration tests are only for checking the context loading
- **JWT authentication** — replace HTTP Basic Auth with JSON Web Tokens. The `user` service - which I ommitted because of it's own responisibilities - would handle login and registration, issuing signed tokens. The product service would validate tokens using a shared secret.
- **User service** — best a different project - full login and registration endpoints, password hashing, user roles stored in the database
- **Image and download URLs** — `imageUrl` and `downloadUrl` fields added to the `Game` entity as placeholders then integrating with an object storage service for file hosting
- **Genre management endpoints** — for now genres are seeded on startup - they are hovewer Entities because there is no fixed list of game genres - it should allow flexibility, there could be an admin endpoint to add new genres without redeployment
