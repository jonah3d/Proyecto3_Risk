package com.proyecto3.risk.controllers.sessioncontrollers;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
public class GameManager {
    private final Map<String, GameSession> games = new ConcurrentHashMap<>();

    public String createGame(PlayerSession hostSession, int maxPlayers, boolean isPublic) {
        String gameId = isPublic ?
                UUID.randomUUID().toString().substring(0, 8) :
                UUID.randomUUID().toString();

        System.out.println(gameId);

        GameSession session = new GameSession(gameId, maxPlayers, isPublic);
        games.put(gameId, session);


        boolean added = session.addPlayer(hostSession);
        if (!added) {

            games.remove(gameId);
            return null;
        }

        return gameId;
    }

    public boolean joinGame(String gameId, PlayerSession playerSession) {
        GameSession game = games.get(gameId);
        if (game == null) {
            return false;
        }

        return game.addPlayer(playerSession);
    }

    public void leaveGame(String gameId, Long playerId) {
        GameSession game = games.get(gameId);
        if (game != null) {
            boolean removed = game.removePlayer(playerId);


            if (removed && game.getCurrentPlayerCount() == 0) {
                games.remove(gameId);
            }
        }
    }

    public void handlePlayerInput(Long playerId, String gameId, JsonObject input) {
        GameSession game = games.get(gameId);
        if (game != null && game.hasPlayer(playerId)) {
            game.handlePlayerInput(playerId, input);
        }
    }

    public List<Map<String, Object>> getPublicGames() {
        return games.values().stream()
                .filter(GameSession::isPublic)
                .filter(game -> game.getState() == GameSession.GameState.WAITING)
                .filter(game -> !game.isFull())
                .map(game -> {
                    Map<String, Object> gameInfo = new HashMap<>();
                    gameInfo.put("id", game.getGameId());
                    gameInfo.put("players", game.getCurrentPlayerCount());
                    gameInfo.put("maxPlayers", game.getMaxPlayers());
                    return gameInfo;
                })
                .collect(Collectors.toList());
    }

    public GameSession getGame(String gameId) {
        return games.get(gameId);
    }

    public GameSession findGameForPlayer(Long playerId) {
        for (GameSession game : games.values()) {
            if (game.hasPlayer(playerId)) {
                return game;
            }
        }
        return null;
    }
}