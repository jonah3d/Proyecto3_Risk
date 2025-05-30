package com.proyecto3.risk.controllers;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.proyecto3.risk.controllers.sessioncontrollers.GameManager;
import com.proyecto3.risk.controllers.sessioncontrollers.GameSession;
import com.proyecto3.risk.controllers.sessioncontrollers.PlayerSession;
import com.proyecto3.risk.controllers.sessioncontrollers.SessionManager;
import com.proyecto3.risk.model.entities.Player;
import com.proyecto3.risk.model.entities.User;
import com.proyecto3.risk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class GameController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private UserService userService;


    private final Gson gson = new Gson();

    public void handleConnect(WebSocketSession session) {
        sessionManager.registerSession(session);


        Map<String, Object> welcome = new HashMap<>();
        welcome.put("action", "connected");
        welcome.put("sessionId", session.getId());
        sessionManager.sendJsonMessage(session, welcome);
    }

    public void handleDisconnect(WebSocketSession session) {
        Player player = sessionManager.getPlayer(session);

        if (player != null) {

            GameSession game = gameManager.findGameForPlayer(player.getId());
            if (game != null) {
                gameManager.leaveGame(game.getToken(), player.getId());
            }
        }

        sessionManager.removeSession(session);
    }

    public void handleMessage(WebSocketSession session, String payload) {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();

            if (!json.has("action")) {
                sendError(session, "Missing 'action' field");
                return;
            }

            String action = json.get("action").getAsString();

            switch (action) {
                case "register":
                    handleRegister(session, json);
                    break;
                case "create_game":
                    handleCreateGame(session, json);
                    break;
                case "join_game":
                    handleJoinGame(session, json);
                    break;
                case "leave_game":
                    handleLeaveGame(session, json);
                    break;
                case "list_games":
                    handleListGames(session);
                    break;
                case "send_input":
                    handleGameInput(session, json);
                    break;
                default:
                    sendError(session, "Unknown action: " + action);
            }
        } catch (JsonSyntaxException e) {
            sendError(session, "Invalid JSON format");
        } catch (Exception e) {
            sendError(session, "Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRegister(WebSocketSession session, JsonObject json) {


      //  String playerName = json.get("name").getAsString();
        Long playerId = json.get("userId").getAsLong();

        User playerUser = userService.getUserById(playerId);
        if (playerUser == null) {
            sendError(session,"Id of player during registration didnt match any user in the db");
        }

        Player player = new Player();
        player.setUser(playerUser);

        player.setId(playerId);
        sessionManager.registerPlayer(session, player);

        Map<String, Object> response = new HashMap<>();
        response.put("action", "registered");
        response.put("playerId", player.getId());
        response.put("playerName", playerUser.getUsername());

        sessionManager.sendJsonMessage(session, response);
    }

    private void handleCreateGame(WebSocketSession session, JsonObject json) {
        Player player = sessionManager.getPlayer(session);
        if (player == null) {
            sendError(session, "You must register before creating a game");
            return;
        }

        if (!json.has("maxPlayers")) {
            sendError(session, "Missing 'maxPlayers' field");
            return;
        }

        int maxPlayers = json.get("maxPlayers").getAsInt();
        boolean isPublic = json.has("isPublic") && json.get("isPublic").getAsBoolean();

        String gameName = json.get("gameName").getAsString();


        if (maxPlayers < 2 || maxPlayers > 10) {
            sendError(session, "maxPlayers must be between 2 and 10");
            return;
        }

        PlayerSession playerSession = new PlayerSession(player, session);
        String token = gameManager.createGame(playerSession, maxPlayers, isPublic, gameName);

        if (token != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("action", "game_created");
            response.put("id", gameManager.getGameId(token));
            response.put("token", token);
            response.put("isPublic", isPublic);
            response.put("maxPlayers", maxPlayers);
            response.put("gameName", gameName);

            sessionManager.sendJsonMessage(session, response);
        } else {
            sendError(session, "Failed to create game");
        }
    }

    private void handleJoinGame(WebSocketSession session, JsonObject json) {
        Player player = sessionManager.getPlayer(session);
        if (player == null) {
            sendError(session, "You must register before joining a game");
            return;
        }

        if (!json.has("token")) {
            sendError(session, "Missing 'token' field");
            return;
        }

        String gameId = json.get("token").getAsString();
        PlayerSession playerSession = new PlayerSession(player, session);

        boolean joined = gameManager.joinGame(gameId, playerSession);



        if (joined) {
          var game =  gameManager.getGame(gameId);
            Map<String, Object> response = new HashMap<>();
            response.put("action", "joined_game");
            response.put("token", gameId);
            response.put("gameName", game.getGameName());
            response.put("maxPlayers", game.getMaxPlayers());
         //   response.put("playerId", playerUser);

            sessionManager.sendJsonMessage(session, response);
        } else {
            sendError(session, "Failed to join game. It may be full or not exist.");
        }
    }

    private void handleLeaveGame(WebSocketSession session, JsonObject json) {
        Player player = sessionManager.getPlayer(session);
        if (player == null) {
            return;
        }

        GameSession game = gameManager.findGameForPlayer(player.getId());
        if (game != null) {
            gameManager.leaveGame(game.getToken(), player.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("action", "left_game");
            response.put("token", game.getToken());

            sessionManager.sendJsonMessage(session, response);
        } else {
            sendError(session, "You are not in a game");
        }
    }

    private void handleListGames(WebSocketSession session) {
        Map<String, Object> response = new HashMap<>();
        response.put("action", "games_list");
        response.put("games", gameManager.getPublicGames());

        sessionManager.sendJsonMessage(session, response);
    }

    private void handleGameInput(WebSocketSession session, JsonObject json) {

        Player player = sessionManager.getPlayer(session);
        if (player == null) {
            sendError(session, "You must register before sending game input");
            return;
        }

        GameSession game = gameManager.findGameForPlayer(player.getId());
        if (game == null) {
            sendError(session, "You are not in a game");
            return;
        }

        if (!json.has("data")) {
            sendError(session, "Missing 'data' field");
            return;
        }

        JsonObject inputData = json.get("data").getAsJsonObject();
        gameManager.handlePlayerInput(player.getId(), game.getToken(), inputData);
    }

    private void sendError(WebSocketSession session, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("action", "error");
        error.put("message", message);

        sessionManager.sendJsonMessage(session, error);
    }
}