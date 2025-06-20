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


### WebSocket Message Flow (Actions and Responses)

Messages are JSON objects containing an ```"action"``` field and additional data.

#### Client Requests (Sent by Frontend):

- ```action: "register"```

  - Request Data: ```{ "userId": <Long> }```

  - Purpose: Associates a connected WebSocket session with a registered user ID.

- ```action: "create_game"```

  - Request Data: ```{ "maxPlayers": <Integer>, "isPublic": <Boolean>, "gameName": <String> }```

  - Purpose: Creates a new game lobby. maxPlayers (2-10).

- ```action: "join_game"```

  - Request Data: ```{ "token": <String> }```

  - Purpose: Joins an existing game lobby using its unique token.

- ```action: "leave_game"```

  - Request Data: (No specific data, player is identified by session)

  - Purpose: Player leaves the current game.

- ```action: "list_games"```

  - Request Data: (No specific data)

  - Purpose: Requests a list of available public games.

- ```action: "send_input"```

  - Request Data: ```{ "data": <JsonObject> }```

  - Purpose: Generic wrapper for in-game actions. The content of data depends on the current game stage.

  - During Occupation/Bonus (```stage: "OCCUPATION" or "BONUS"```):

    - data: ```{ "type": "place_troops", "countryId": <Long>, "troops": <Integer> }```

    - Purpose: Places a specified number of troops on a controlled territory.

  - During Attacking (```stage: "ATTACKING"```):

    - data: ```{ "type": "attack", "countryId": <Long> (source), "enemyCountryId": <Long> (target), "troops": <Integer> (attacking troops) }```

    - Purpose: Initiates an attack from ```sourceCountryId``` to ```enemyCountryId``` with ```troops```.

    - data: ```{ "type": "end_attack" }```

    - Purpose: Ends the attacking phase for the current player.

    - data: ```{ "type": "move_troops", "troops": <Integer> }```

    - Purpose: Moves troops into a newly conquered territory after a successful attack.

  - During Reinforcement (```stage: "REFORCE"```):

    - data: ```{ "type": "fortify", "sourceCountryId": <Long>, "targetCountryId": <Long>, "troops": <Integer> }```

    - Purpose: Moves troops between two connected, player-controlled territories.

    - data: ```{ "type": "end_turn" }```

    - Purpose: Ends the current player's turn, transitioning to the next player.

#### Server Responses (Sent by Backend):

- ```action: "connected"```

  - Data: ```{ "sessionId": <String> }```

  - Purpose: Confirms successful WebSocket connection.

- ```action: "registered"```

  - Data: ```{ "playerId": <Long>, "playerName": <String> }```

  - Purpose: Confirms player registration within the session manager.

- ```action: "game_created"```

  - Data: ```{ "id": <Long>, "token": <String>, "isPublic": <Boolean>, "maxPlayers": <Integer>, "gameName": <String> }```

  - Purpose: Confirms game creation for the host player.

- ```action: "joined_game"```

  - Data: ```{ "token": <String>, "gameName": <String>, "maxPlayers": <Integer> }```

  - Purpose: Confirms successful joining of a game for the player.

- ```action: "left_game"```

  - Data: ```{ "token": <String> }```

  - Purpose: Notifies the player they have left a game.

- ```action: "player_left"```

  - Data: ```{ "player_id": <Long> }```

  - Purpose: Broadcast to all players in a game that a specific player has left.

- ```action: "games_list"```

  - Data: ```{ "games": [ { "id": <Long>, "token": <String>, "players": <Integer>, "maxPlayers": <Integer>, "gameName": <String> }, ... ] }```

  - Purpose: Provides a list of available public games.

- ```action: "game_started"```

  - Data: (No specific data)

  - Purpose: Broadcast to all players that the game has started.

- ```action: "player_list"```

  - Data: ```{ "players": [ { "id": <Long>, "username": <String>, "avatar_url": <String> }, ... ] }```

  - Purpose: Updates all players with the current list of players in the lobby/game.

- ```action: "player_turn"```

  - Data: ```{ "playerId": <Long> }```

  - Purpose: Broadcasts whose turn it is.

- ```action: "map_update"```

  - Data: ```{ "countries": [ { "countryId": <Long>, "troops": <Integer>, "playerId": <Long> (owner, optional) }, ... ] }```

  - Purpose: Provides a full update of the game board's territory ownership and troop counts.

- ```action: "bonus_to_place"```

  - Data: ```{ "bonusTroops": <Integer>, "totalTroopsToPlace": <Integer>, "playerId": <Long>, "message": <String> }```

  - Purpose: Notifies the current player about bonus troops received at the start of their turn.

- ```action: "troops_placed"```

  - Data: ```{ "countryId": <Long>, "troopsPlaced": <Integer>, "remainingTroops": <Integer> }```

  - Purpose: Confirms troop placement for the current player during occupation/bonus phases.

- ```action: "no_bonus"```

  - Data: ```{ "message": <String> }```

  - Purpose: Notifies player that no bonus troops were received.

- ```action: "attack_in_progress"```

  - Data: ```{ "attackerId": <Long>, "defenderId": <Long>, "sourceCountryId": <Long>, "targetCountryId": <Long>, "attackingTroops": <Integer>, "defendingTroops": <Integer> }```

  - Purpose: Broadcasts details of an ongoing attack to all spectators.

- ```action: "territory_under_attack"```

  - Data: ```{ "attackerId": <Long>, "sourceCountryId": <Long>, "targetCountryId": <Long>, "attackingTroops": <Integer>, "defendingTroops": <Integer> }```

  - Purpose: Sent specifically to the defending player to notify them of an incoming attack.

- ```action: "attack_initiated"```

  - Data: ```{ "targetCountryId": <Long>, "defenderId": <Long>, "attackingTroops": <Integer>, "defendingTroops": <Integer> }```

  - Purpose: Confirms attack initiation to the attacking player.

- ```action: "dice_rolls"```

  - Data: ```{ "attackerDice": [<Int>, ...], "defenderDice": [<Int>, ...] }```

  - Purpose: Broadcasts the dice rolls for both attacker and defender during combat.

- ```action: "attack_result"```

  - Data: ```{ "attackerLosses": <Integer>, "defenderLosses": <Integer>, "sourceCountryTroopsRemaining": <Integer>, "targetCountryTroopsRemaining": <Integer> }```

  - Purpose: Broadcasts the outcome of a combat round (losses for both sides).

- ```action: "territory_conquered"```

  - Data: ```{ "attackerId": <Long>, "defenderId": <Long>, "territoryId": <Long> }```

  - Purpose: Broadcasts when a territory is successfully conquered.

- ```action: "move_troops"```

  - Data: ```{ "message": <String>, "sourceCountryId": <Long>, "targetCountryId": <Long>, "maxTroops": <Integer> }```

  - Purpose: Prompts the attacking player to move troops into a newly conquered territory.

- ```action: "fortification"```

  - ```Data: { "playerId": <Long>, "sourceCountryId": <Long>, "targetCountryId": <Long>, "troops": <Integer> }```

  - Purpose: Broadcasts details of a troop fortification move.

- ```action: "stage_change"```

  - Data: ```{ "stage": <String> ("OCCUPATION", "ATTACKING", "REFORCE", "BONUS"), "playerId": <Long> (current player, optional) }```
  
  - Purpose: Notifies clients about changes in the overall game stage.

- ```action: "game_state"```

  - Data: ```{ "state": <String> ("WAITING", "PLAYING", "FINISHED") }```

  - Purpose: Broadcasts changes in the overall game state.

- ```action: "lose"```

  - Data: ```{ "message": <String>, "player_id": <Long> (optional for broadcast) }```

  - Purpose: Notifies a player they have lost or broadcasts that a player has lost.

- ```action: "win"```

  - Data: ```{ "message": <String> }```

  - Purpose: Sent to the winning player.

- ```action: "game_over"```

  - Data: ```{ "winner": <Long>, "message": <String> }```

  - Purpose: Broadcast to all players when the game has concluded with a winner.

- ```action: "error"```

  - Data: ```{ "message": <String> }```

  - Purpose: Generic error message.

### Game Phases
The game progresses through distinct states and stages, managed by the GameSession class:

#### GameState (GameSession.GameState)
- ```WAITING```: Players are joining the game lobby.

- ```PLAYING```: The game is actively in progress.

- ```FINISHED```: The game has concluded (e.g., a winner has been determined, or all but one player left).

#### GameStage (GameSession.GameStage)
These stages occur sequentially within a player's turn during the PLAYING state:

- ```OCCUPATION```: Initial phase where players place their starting troops to occupy territories.

- ```BONUS```: Players receive bonus troops based on controlled territories and continents. This is the first stage of each player's turn after the initial occupation phase.

- ```ATTACKING```: Players can attack adjacent enemy territories.

- ```REFORCE```: Players can fortify their positions by moving troops between their own connected territories.

#### AttackPhase (GameSession.AttackPhase)
Sub-phases within the ```ATTACKING stage```:

- ```SELECTING_ATTACK```: Player chooses territories to attack from and to.

- ```MOVING_TROOPS```: After conquering a territory, the player must move troops from the attacking country to the newly occupied one.

- ```FINISHED```: The attacking phase is complete for the current player.

### Data Transfer Objects (DTOs)
The backend utilizes Data Transfer Objects (DTOs) to define the structure of data exchanged between the client and the server, ensuring efficient and type-safe communication. These DTOs typically mirror a subset of the entity properties, tailored for specific API operations.

- AvatarResponseDto
- BorderResponseDto
- CardResponseDto
- ContinentResponseDto
- CountryResponseDto
- LoginRequestDto
- UpdateUserDto
- UserRegistrationDto
- UserResponseDto

## Dockerization

The project includes a Dockerfile for easy containerization, allowing the backend to be deployed consistently across various environments.
```

FROM maven:3.9.9-eclipse-temurin-24-alpine as build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk
COPY --from=build /target/*.jar risk.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/risk.jar"]


```

You do not need to build the docker on your local machine since its intended use is to deploy the backend to Railway cloud.

The application will be accessible at ```http://localhost:8080```.
Since at the time you might run this, the backend will be offline. 
- You should clone this repository (Preferably in IntelliJ)
- Run the dcl and ddl script provided in a mysql database.
- Fill the ```application.properties``` file with the necessary information.
- Run the application
