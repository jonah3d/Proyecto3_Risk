package com.proyecto3.risk.controllers.sessioncontrollers;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.proyecto3.risk.model.entities.Country;
import com.proyecto3.risk.model.entities.Occupy;
import com.proyecto3.risk.repository.CountryRepository;
import com.proyecto3.risk.service.CountryService;
import com.proyecto3.risk.service.CountryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private GameStage stage;
    private Thread gameThread;
    List<Country> allCountrys = new ArrayList<>();
    private Map<Long, List<Occupy>> occupies = new ConcurrentHashMap<>();
    private final Map<Long, Integer> troopsToPlace = new ConcurrentHashMap<>();


    private final CountryService countryService;

    public enum GameState {
        WAITING,
        PLAYING,
        FINISHED
    }

    public enum GameStage {
        OCCUPATION,
        ATTACKING,
        REFORCE
    }



    public GameSession(String token, int maxPlayers, boolean isPublic, String gameName, Long id, CountryService countryService) {
        this.token = token;
        this.maxPlayers = maxPlayers;
        this.isPublic = isPublic;
        this.id = id;
        this.gameName = gameName;
        this.countryService = countryService;

        allCountrys.clear();
        allCountrys = countryService.getAllCountries();

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
                if (playerId.equals(currentPlayerId)) {
                    nextTurn();
                }

            }


            if (players.isEmpty()) {
                endGame();
                return true;
            }


            broadcastPlayerList();

            Map<String,Object> leavePlayers = new HashMap<>();
            leavePlayers.put("action","player_left");
            leavePlayers.put("player_id",playerId);

            broadcast(leavePlayers);
            nextTurn();
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
        stage = GameStage.OCCUPATION;

        int initialTroops = calculateNumOfTroops(maxPlayers);
        for (Long playerId : players.keySet()) {
            troopsToPlace.put(playerId, initialTroops);
        }

        Map<String, Object> gameStartMessage = new HashMap<>();
        gameStartMessage.put("action", "game_started");
        broadcast(gameStartMessage);
        sendMapUpdate();

        chooseInitialPlayer();
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this::runGameLoop);
            gameThread.start();
        }
    }


    private void startOccupation() {
        stage = GameStage.OCCUPATION;
    }

    private void runGameLoop() {
        try {

            while (state == GameState.PLAYING) {
                switch (stage) {
                    case OCCUPATION:
                        int troops_num = calculateNumOfTroops(maxPlayers);

                        break;
                    case ATTACKING:
                        // handle attacking logic
                        break;
                    case REFORCE:
                        // handle reforce logic
                        break;
                }

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
            sendToPlayer(playerId, Map.of(
                    "action", "error",
                    "message", "It's not your turn"
            ));
            return;
        }

        if (stage == GameStage.OCCUPATION) {
            handleOccupationInput(playerId, input);
            return;
        }



        broadcastGameState();
    }

    private void handleOccupationInput(Long playerId, JsonObject input) {
        if (!input.has("countryId")) {
            sendToPlayer(playerId, Map.of(
                    "action", "error",
                    "message", "Missing 'countryId' for occupation"
            ));
            return;
        }

        long countryId = input.get("countryId").getAsLong();
        int numoftroops = input.get("troops").getAsInt();
        if(numoftroops!=1){
            sendToPlayer(playerId, Map.of(
                    "action", "error",
                    "message", "You can only place one troop"
            ));
            return;
        }


        boolean alreadyOccupied = occupies.values().stream()
                .flatMap(List::stream)
                .anyMatch(o -> o.getCountryId() == countryId && !o.getPlayerId().equals(playerId));

        if (alreadyOccupied) {
            sendToPlayer(playerId, Map.of(
                    "action", "error",
                    "message", "Territory already occupied by another player"
            ));
            return;
        }


        occupies.computeIfAbsent(playerId, k -> new ArrayList<>())
                .add(new Occupy(playerId, countryId, numoftroops));


        troopsToPlace.put(playerId, troopsToPlace.get(playerId) - 1);


        boolean allDone = troopsToPlace.values().stream().allMatch(t -> t <= 0);

        if (allDone) {
            stage = GameStage.ATTACKING;
            broadcast(Map.of("action", "stage_change", "stage", "ATTACKING"));
        } else {
            Map<String, Object> occupationUpdate = new HashMap<>();
            occupationUpdate.put("action", "territory_occupied");
            occupationUpdate.put("playerId", playerId);
            occupationUpdate.put("territoryId", countryId);
            occupationUpdate.put("troops", numoftroops);
            broadcast(occupationUpdate);
            nextTurn();
        }


    }



    private void sendMapUpdate() {
        Map<Long, Integer> countryTroopMap = new HashMap<>();
        Map<Long, Long> countryOwnerMap = new HashMap<>();

        // Initialize all countries with zero troops and no owner
        for (Country country : allCountrys) {
            countryTroopMap.put(country.getId(), 0);
            countryOwnerMap.put(country.getId(), null);  // No owner initially
        }

        // Update the maps with occupation data
        for (List<Occupy> occupyList : occupies.values()) {
            for (Occupy occupy : occupyList) {
                long countryId = occupy.getCountryId();
                int troops = occupy.getTroops();
                long playerId = occupy.getPlayerId();

                countryTroopMap.merge(countryId, troops, Integer::sum);
                // Set or update the owner of the country
                countryOwnerMap.put(countryId, playerId);
            }
        }

        // Create the list of country data to send
        List<Map<String, Object>> countries = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : countryTroopMap.entrySet()) {
            Long countryId = entry.getKey();
            Map<String, Object> countryData = new HashMap<>();
            countryData.put("countryId", countryId);
            countryData.put("troops", entry.getValue());

            // Include the player ID who owns this country (may be null if unoccupied)
            Long ownerId = countryOwnerMap.get(countryId);
            if (ownerId != null) {
                countryData.put("playerId", ownerId);
            }

            countries.add(countryData);
        }

        // Broadcast the map update to all players
        Map<String, Object> message = new HashMap<>();
        message.put("action", "map_update");
        message.put("countries", countries);

        broadcast(message);
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

    public void sendToPlayer(Long playerId, Object message) {
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
        sendMapUpdate();

    }

    private int calculateNumOfTroops(int maxPlayers) {
int numOfTroops = 0;

        switch (maxPlayers) {
            case 2:
                numOfTroops = 40;
            break;
            case 3:
                numOfTroops = 35;
            break;
            case 4:
                numOfTroops = 30;
            break;
            case 5:
                numOfTroops = 25;
            break;
            case 6:
                numOfTroops = 20;
            break;
        }

        return numOfTroops;
    }

}
