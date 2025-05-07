package com.proyecto3.risk.controllers.sessioncontrollers;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.proyecto3.risk.model.entities.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameSession {
    private final String gameId;
    private final int maxPlayers;
    private final boolean isPublic;
    private final String gameName;
    private final Map<Long, PlayerSession> players = new ConcurrentHashMap<>();
    private GameState state = GameState.WAITING;
    private Thread gameThread;

    public enum GameState {
        WAITING,
        PLAYING,
        FINISHED
    }

    public GameSession(String gameId, int maxPlayers, boolean isPublic, String gameName) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.isPublic = isPublic;
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public synchronized boolean addPlayer(PlayerSession playerSession) {
        if (state != GameState.WAITING || players.size() >= maxPlayers) {
            return false;
        }

        //Player player = playerSession.getPlayer();
        if (playerSession == null || playerSession.getPlayer() == null || playerSession.getPlayer().getId() == null) {
            return false;
        }
        players.put(playerSession.getPlayer().getId(), playerSession);


        broadcastPlayerList();


        if (players.size() >= maxPlayers) {
            startGame();
        }

        return true;
    }

    public synchronized boolean removePlayer(Long playerId) {
        PlayerSession removed = players.remove(playerId);
        if (removed != null) {

            if (state == GameState.PLAYING) {
                // Handle game-specific logic for player disconnection
            }


            if (players.isEmpty()) {
                endGame();
                return true;
            }


            broadcastPlayerList();
            return true;
        }
        return false;
    }

    private void broadcastPlayerList() {
        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("action", "player_list");
        playerListMessage.put("players", players.values().stream()
                .map(ps -> ps.getPlayer().getId())
                .collect(Collectors.toList()));

        broadcast(playerListMessage);
    }

    private void startGame() {
        state = GameState.PLAYING;

        Map<String, Object> gameStartMessage = new HashMap<>();
        gameStartMessage.put("action", "game_started");
        broadcast(gameStartMessage);


        gameThread = new Thread(this::runGameLoop);
        gameThread.start();
    }

    private void runGameLoop() {
        try {
            // Your game logic goes here
            // For example:
            while (state == GameState.PLAYING) {
                // Game update tick
                // Process game events
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            endGame();
        }
    }

    private void endGame() {
        if (state == GameState.FINISHED) {
            return;
        }

        state = GameState.FINISHED;


        Map<String, Object> gameEndMessage = new HashMap<>();
        gameEndMessage.put("action", "game_ended");
        broadcast(gameEndMessage);


        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
    }

    public void handlePlayerInput(Long playerId, JsonObject input) {
        if (state != GameState.PLAYING) {
            return;
        }

        // Process player input based on your game logic
        // For example:
        // - Update player position
        // - Handle player actions
        // - etc.

        // Then broadcast game state updates
        broadcastGameState();
    }

    private void broadcastGameState() {
        // Create a game state update to send to all players
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("action", "game_state");
        // Add relevant game state data

        broadcast(gameState);
    }

    public void broadcast(Object message) {
        String json = new Gson().toJson(message);
        for (PlayerSession playerSession : players.values()) {
            playerSession.sendMessage(json);
        }
    }

    public void sendToPlayer(String playerId, Object message) {
        PlayerSession playerSession = players.get(playerId);
        if (playerSession != null) {
            playerSession.sendJsonMessage(message);
        }
    }

    public boolean hasPlayer(Long playerId) {
        return players.containsKey(playerId);
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public GameState getState() {
        return state;
    }

    public int getCurrentPlayerCount() {
        return players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Collection<PlayerSession> getPlayers() {
        return players.values();
    }
}