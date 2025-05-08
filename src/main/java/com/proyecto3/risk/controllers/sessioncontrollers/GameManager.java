package com.proyecto3.risk.controllers.sessioncontrollers;

import com.nimbusds.jose.shaded.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
public class GameManager {
    private final Map<String, GameSession> games = new ConcurrentHashMap<>();

    public String createGame(PlayerSession hostSession, int maxPlayers, boolean isPublic,String gameName) {
        String token = isPublic ?
                UUID.randomUUID().toString().substring(0, 8) :
                UUID.randomUUID().toString();

        long gameId = 10000 + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 90000);


        System.out.println(token);

        GameSession session = new GameSession(token, maxPlayers, isPublic,gameName,gameId);
        games.put(token, session);


        boolean added = session.addPlayer(hostSession);
        if (!added) {

            games.remove(token);
            return null;
        }

        return token;
    }

    public Long getGameId(String token) {
        GameSession session = games.get(token);
        return session.getId();
    }

    public boolean joinGame(String token, PlayerSession playerSession) {
        GameSession game = games.get(token);
        if (game == null) {
            return false;
        }

        return game.addPlayer(playerSession);
    }

    public void leaveGame(String token, Long playerId) {
        GameSession game = games.get(token);
        if (game != null) {
            boolean removed = game.removePlayer(playerId);


            if (removed && game.getCurrentPlayerCount() == 0) {
                games.remove(token);
            }
        }
    }

    public void handlePlayerInput(Long playerId, String token, JsonObject input) {
        GameSession game = games.get(token);
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
                    gameInfo.put("id", game.getId());
                    gameInfo.put("token", game.getToken());
                    gameInfo.put("players", game.getCurrentPlayerCount());
                    gameInfo.put("maxPlayers", game.getMaxPlayers());
                    gameInfo.put("gameName", game.getGameName());
                    return gameInfo;
                })
                .collect(Collectors.toList());
    }

    public GameSession getGame(String token) {
        return games.get(token);
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
