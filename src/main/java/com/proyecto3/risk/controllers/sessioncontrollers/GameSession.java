package com.proyecto3.risk.controllers.sessioncontrollers;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameSession {
    private final String token;
    private final int maxPlayers;
    private final boolean isPublic;
    private final Long id;
    private final String gameName;
    private final Map<Long, PlayerSession> players = new ConcurrentHashMap<>();
    private GameState state = GameState.WAITING;
    private Thread gameThread;

    public enum GameState {
        WAITING,
        PLAYING,
        FINISHED
    }

    public GameSession(String token, int maxPlayers, boolean isPublic,  String gameName,Long id) {
        this.token = token;
        this.maxPlayers = maxPlayers;
        this.isPublic = isPublic;
        this.id = id;
        this.gameName = gameName;
    }

    public String getToken() {
        return token;
    }

    public String getGameName() {
        return gameName;
    }

    public Long getId() {
        return id;
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

            }


            if (players.isEmpty()) {
                endGame();
                return true;
            }


            broadcastPlayerList();
            broadcast("Player " + playerId + " left the game");
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


        chooseInitialPlayer();
        gameThread = new Thread(this::runGameLoop);
        gameThread.start();
    }

    private void runGameLoop() {
        try {

            while (state == GameState.PLAYING) {

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

        if (!playerId.equals(currentPlayerId)) {
            sendToPlayer(playerId.toString(), Map.of(
                    "action", "error",
                    "message", "It's not your turn"
            ));
            return;
        }
        // Example: process input
    /*    String move = input.get("move").getAsString();
        System.out.println("Player " + playerId + " made move: " + move);

        // Update game state here...

        // Then move to next player's turn*/
        nextTurn();

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


    private Long currentPlayerId;
    private void chooseInitialPlayer() {
        List<Long> playerIds = new ArrayList<>(players.keySet());
        if (playerIds.isEmpty()) return;

        Collections.shuffle(playerIds);
        currentPlayerId = playerIds.get(0);

        Map<String, Object> turnMessage = new HashMap<>();
        turnMessage.put("action", "player_turn");
        turnMessage.put("playerId", currentPlayerId);
        broadcast(turnMessage);
    }



    private void nextTurn() {
        List<Long> playerIds = new ArrayList<>(players.keySet());
        if (playerIds.isEmpty()) return;

        int index = playerIds.indexOf(currentPlayerId);
        currentPlayerId = playerIds.get((index + 1) % playerIds.size());

        Map<String, Object> turnMessage = new HashMap<>();
        turnMessage.put("action", "player_turn");
        turnMessage.put("playerId", currentPlayerId);
        broadcast(turnMessage);
    }

}
