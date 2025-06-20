# A Spring Boot Backend For A Multiplayer; Multiplatform Risk Game

## Project Brief

As this was the third and final project. The objective was to work with my course mate and also other students specialising in web development. After several rounds of voting we ended up with the Risk board game. We were supposed to work in a three-man group with varied specializations (Backend, Frontend - WPF, Frontend - Web).

This repository contains the backend implementation for a multiplayer, multiplatform digital adaptation of the classic board game, Risk. It provides robust RESTful APIs for user management and game setup, alongside real-time WebSocket communication for core gameplay mechanics.

## Tech Stack

- Language: Java 21

- Framework: Spring Boot 3
  - Spring Data JPA for database interaction
  - Spring Security for authentication and authorization
  - Spring WebSockets for real-time game communication

- Database: MySQL 9

- ORM: Hibernate

- Dependency Management: Maven

- Utility Libraries:

  - ModelMapper for DTO to Entity mapping

  - Gson for JSON serialization/deserialization in WebSockets

- Containerization: Docker (Alpine Linux base image for lean deployment)

- Hosting: Railway Cloud

Frontend (as per presentation)
- Web Client: React, Axios, Zustand <a href="https://github.com/Markitus01/RISK">React Frontend Repo</a>

- Desktop Client: WPF (.NET), C# <a href="https://github.com/BruPotrony/Risk">Wpf Frontend Repo</a>

## Architecture Overview

The backend is designed with a clear, layered architecture typical of Spring Boot applications, separating concerns into controllers, services, and repositories. 
Real-time game logic is handled through a dedicated WebSocket module, providing a responsive and dynamic gameplay experience.

1. HTTP REST: Used for initial, punctual requests such as user registration, login, fetching static game data (avatars, continents, countries, borders, cards), and user profile updates. These requests return JSON responses.

2. WebSockets: Once a user is authenticated and enters the game lobby or a game session, a persistent WebSocket connection is established. This allows for bidirectional, real-time communication essential for game actions
   (e.g., placing troops, attacking, moving units, turn progression) and immediate updates to all connected players.

### Backend Details

#### Directory Structure

```

├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── proyecto3
│   │   │           └── risk
│   │   │               ├── configurations    // Spring Security, ModelMapper, WebSocket config
│   │   │               ├── controllers       // REST API endpoints
│   │   │               │   └── sessioncontrollers // WebSocket-related game logic controllers
│   │   │               ├── exceptions        // Custom exceptions
│   │   │               ├── model             // JPA Entities and DTOs
│   │   │               ├── repository        // Spring Data JPA repositories
│   │   │               └── service           // Business logic services
│   │   └── resources
│   │       └── application.properties        // Application configurations
│   └── test
│       └── java
│           └── com
│               └── proyecto3
│                   └── risk                  // Unit and Integration Tests
├── pom.xml                                   // Maven Project Object Model
├── Dockerfile                                // Docker build instructions
└── README.md                                 // This file



```

#### Database Schema

The application interacts with a MySQL database. The core entities and their relationships are depicted in the UML diagram found in the project presentation. 
Key tables include:

- ```users```: Stores user authentication and profile information (username, password, email, first name, last name, avatar, wins, games played).

- ```avatars```: Contains predefined character skins for users.

- ```partida``` (Game): Represents a game instance, including max players, public/private status, and current turn state.

- ```players```: Links users to active game sessions, tracking their state within a specific game.

- ```continents```: Defines continents on the game board and their bonus troops.

- ```countries```: Represents individual territories on the map, belonging to continents.

- ```borders```: Defines adjacencies between countries.

- ```cards```: Game cards, each linked to a country and a card type.

- ```occupy```: Tracks which player occupies which country and with how many troops during an active game.


#### REST Endpoints
The REST API primarily handles user management, authentication, and fetching static game data.

1. User Management (```/api/users```)
- ```POST /api/users/register```

  - Functionality: Registers a new user.

  - Request Body: UserRegistrationDto (username, password, email, firstName, lastName, avatarId).

  - Response: ```201 CREATED``` with ```UserResponseDto``` if successful, ```400 BAD_REQUEST``` or ```500 INTERNAL_SERVER_ERROR``` otherwise.

  - Authentication: ```permitAll()```

- ```PUT /api/users/update/{username}```

  - Functionality: Updates an existing user's profile. Requires the authenticated user to be the owner of the profile.

  - Request Body: UpdateUserDto (email, firstName, lastName, avatarId, optional new password).

  - Response: ```200 OK``` with UserResponseDto if successful, 404 NOT_FOUND, 400 BAD_REQUEST, or 500 INTERNAL_SERVER_ERROR otherwise.

  - Authentication: authenticated() with @IsSameUser custom security annotation.

- ```DELETE /api/users/delete/{username}```

  - Functionality: Deletes a user account. Requires the authenticated user to be the owner of the profile and provide their password for confirmation.

  - Request Params: password

  - Response: ```200 OK```if successful, 401 UNAUTHORIZED, 400 BAD_REQUEST otherwise.

  - Authentication: authenticated() with @IsSameUser custom security annotation.

- ```GET /api/users```

  - Functionality: Retrieves a list of all registered users. (Intended for admin/debugging purposes).

  - Response: ```200 OK``` with List<UserResponseDto>, ```204 NO_CONTENT``` if no users found.

  - Authentication: permitAll() (configured for ease of testing).

- ```GET /api/users/{username}```

  - Functionality: Retrieves a single user's profile by username.

  - Response: ```200 OK```with UserResponseDto, ```404 NOT_FOUND``` if user not found.

  - Authentication: permitAll()

- ```GET /api/users/id/{userId}```

  - Functionality: Retrieves a single user's profile by ID.

  - Response: ```200 OK``` with UserResponseDto, ```404 NOT_FOUND``` if user not found.

  - Authentication: ```permitAll()```

- ```GET /api/users/{userId}/wins```

  - Functionality: Retrieves the number of wins for a given user ID.

  - Response: ```200 OK``` with Integer count, ```404 NOT_FOUND``` if user not found.

  - Authentication: ```permitAll()```

- ```GET /api/users/{userId}/games```

  - Functionality: Retrieves the number of games played for a given user ID.

  - Response: ```200 OK``` with Integer count, ```404 NOT_FOUND``` if user not found.

  - Authentication: ```permitAll()```


2. Authentication (```/api/login```)
- ```POST /api/login```

  - Functionality: Authenticates a user.

  - Request Body: LoginRequestDto (username, password).

  - Response: ```200 OK``` with UserResponseDto if credentials are valid, ```401 UNAUTHORIZED``` otherwise.

  - Authentication: ```permitAll()```


3. Avatar Endpoints (```/api/avatars```)
- ```GET /api/avatars```

  - Functionality: Retrieves a list of all available avatars.

  - Response: ```200 OK``` with List<AvatarResponseDto>, ```204 NO_CONTENT``` if no avatars found.

  - Authentication: ```permitAll()```

- ```GET /api/avatars/{name}```

  - Functionality: Retrieves an avatar by its name.

  - Response: ```200 OK``` with AvatarResponseDto, ```400 BAD_REQUEST``` or ```404 NOT_FOUND``` if not found.

  - Authentication: ```permitAll()```

4. Game Data Endpoints (```/api/countries```, ```/api/continents```, ```/api/cards```, ```/api/borders```)
These endpoints provide static game configuration data:

- ```GET /api/countries```

Retrieves all countries.

- ```GET /api/countries/{name}```

Retrieves a country by name.

- ```GET /api/countries/id/{id}```

Retrieves a country by ID.

- ```GET /api/continents```

Retrieves all continents.

- ```GET /api/continents/{name}```

Retrieves a continent by name.

- ```GET /api/continents/id/{id}```

Retrieves a continent by ID.

- ```GET /api/cards```

Retrieves all game cards.

```GET /api/cards/{name}```

Retrieves a card by country name.

- ```GET /api/borders```

Retrieves all border relationships between countries.

- ```GET /api/border/{country1Id}/{country2Id}```

Retrieves a specific border by two country IDs.

- ```GET /api/border/{countryId}```

Retrieves all borders connected to a specific country ID.

All these static data endpoints respond with 200 OK and a list/object DTO, or 
appropriate ```404 NOT_FOUND```/```204 NO_CONTENT```/```400 BAD_REQUEST``` statuses. All are configured as ```permitAll()```.
