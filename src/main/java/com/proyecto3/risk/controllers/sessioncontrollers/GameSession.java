package com.proyecto3.risk.controllers.sessioncontrollers;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.proyecto3.risk.model.entities.Border;
import com.proyecto3.risk.model.entities.Country;
import com.proyecto3.risk.model.entities.Occupy;
import com.proyecto3.risk.repository.CountryRepository;
import com.proyecto3.risk.service.BorderService;
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
    private final BorderService borderService;

    public enum GameState {
        WAITING, PLAYING, FINISHED
    }

    public enum GameStage {
        OCCUPATION, ATTACKING, REFORCE
    }


    public GameSession(String token, int maxPlayers, boolean isPublic, String gameName, Long id, CountryService countryService, BorderService borderService) {
        this.token = token;
        this.maxPlayers = maxPlayers;
        this.isPublic = isPublic;
        this.id = id;
        this.gameName = gameName;
        this.countryService = countryService;
        this.borderService = borderService;

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

            Map<String, Object> leavePlayers = new HashMap<>();
            leavePlayers.put("action", "player_left");
            leavePlayers.put("player_id", playerId);

            broadcast(leavePlayers);
            nextTurn();
            return true;
        }
        return false;
    }

    private void broadcastPlayerList() {
        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("action", "player_list");
        playerListMessage.put("players", players.values().stream().map(ps -> ps.getPlayer().getId()).collect(Collectors.toList()));

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


      // autoFillTerritories();


        if (stage == GameStage.OCCUPATION) {
            sendMapUpdate();
            chooseInitialPlayer();
        }

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
            sendToPlayer(playerId, Map.of("action", "error", "message", "It's not your turn"));
            return;
        }

        if (stage == GameStage.OCCUPATION) {
            handleOccupationInput(playerId, input);
            return;
        }
        if (stage == GameStage.ATTACKING) {
            handleAttackingInput(playerId, input);
        }


        broadcastGameState();
    }

    boolean checkAttackingInput(Long playerId, JsonObject input) {
        if (!input.has("type")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'type' field"));
            return false;
        }

        String type = input.get("type").getAsString();
        if (!type.equals("attacking")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Invalid action type"));
            return false;
        }

        if (!input.has("countryId")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'countryId' field"));
            return false;
        }

        if (!input.has("troops")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'troops' field"));
            return false;
        }

        return true;
    }

    private void handleAttackingInput(Long playerId, JsonObject input) {


        if (checkAttackingInput(playerId, input)) {

            long sourceCountryId = input.get("countryId").getAsLong();
            int attackingTroops = input.get("troops").getAsInt();


            List<Occupy> playerOccupies = occupies.get(playerId);
            if (playerOccupies == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You do not occupy any countries."));
                return;
            }


            Occupy sourceOccupy = playerOccupies.stream().filter(o -> o.getCountryId() == sourceCountryId).findFirst().orElse(null);

            if (sourceOccupy == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You do not occupy this country."));
                return;
            }

            int currentTroops = sourceOccupy.getTroops();

            if (currentTroops - attackingTroops < 1) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You must leave at least one troop behind."));
                return;
            }


            List<Border> borders = borderService.findByCountryId(sourceCountryId);

            List<Long> attackableZones = new ArrayList<>();

            for (Border border : borders) {
                long neighborId = border.getCountry1Id().equals(sourceCountryId) ? border.getCountry2Id() : border.getCountry1Id();

                boolean occupiedByEnemy = occupies.entrySet().stream().anyMatch(entry -> !entry.getKey().equals(playerId) && entry.getValue().stream().anyMatch(o -> o.getCountryId().equals(neighborId)));

                if (occupiedByEnemy) {
                    attackableZones.add(neighborId);
                }
            }

            if (attackableZones.isEmpty()) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "No enemies around this country to attack."));
                return;
            }


            sendToPlayer(playerId, Map.of("action", "valid_attack", "from", sourceCountryId, "attackableZones", attackableZones,
                    //   "availableTroops", currentTroops - 1
            ));

            int attackingTerritory = recieveAttackingTerritory(attackableZones);

        }


    }


    private void handleOccupationInput(Long playerId, JsonObject input) {
        if (!input.has("countryId")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'countryId' for occupation"));
            return;
        }

        long countryId = input.get("countryId").getAsLong();
        int numoftroops = input.get("troops").getAsInt();
        if (numoftroops != 1) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You can only place one troop"));
            return;
        }


        boolean alreadyOccupied = occupies.values().stream().flatMap(List::stream).anyMatch(o -> o.getCountryId() == countryId && !o.getPlayerId().equals(playerId));

        if (alreadyOccupied) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Territory already occupied by another player"));
            return;
        }


        occupies.computeIfAbsent(playerId, k -> new ArrayList<>()).add(new Occupy(playerId, countryId, numoftroops));


        troopsToPlace.put(playerId, troopsToPlace.get(playerId) - 1);


        boolean allDone = troopsToPlace.values().stream().allMatch(t -> t <= 0);

        if (allDone) {
            stage = GameStage.ATTACKING;
            broadcast(Map.of("action", "stage_change", "stage", "ATTACKING"));
            nextTurn();
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

        Map<String, Object> gameState = new HashMap<>();
        gameState.put("action", "game_state");


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


    private void autoFillTerritories() {
        System.out.println("Auto-filling territories for testing...");


        occupies.clear();


        List<Country> allCountriesCopy = new ArrayList<>(allCountrys);
        Collections.shuffle(allCountriesCopy);


        List<Long> playerIds = new ArrayList<>(players.keySet());
        if (playerIds.isEmpty()) {
            System.out.println("No players to auto-fill territories for!");
            return;
        }

        System.out.println("Assigning territories to " + playerIds.size() + " players...");


        int countriesPerPlayer = allCountriesCopy.size() / playerIds.size();
        int remainingCountries = allCountriesCopy.size() % playerIds.size();

        int countryIndex = 0;


        for (int i = 0; i < playerIds.size(); i++) {
            Long playerId = playerIds.get(i);
            int territoriesToAssign = countriesPerPlayer + (i < remainingCountries ? 1 : 0);


            List<Occupy> playerOccupies = new ArrayList<>();


            for (int j = 0; j < territoriesToAssign && countryIndex < allCountriesCopy.size(); j++) {
                Country country = allCountriesCopy.get(countryIndex++);


                int troopsPerTerritory = 3;

                playerOccupies.add(new Occupy(playerId, country.getId(), troopsPerTerritory));
                System.out.println("Assigned country " + country.getId() + " to player " + playerId + " with " + troopsPerTerritory + " troops");
            }


            occupies.put(playerId, playerOccupies);


            troopsToPlace.put(playerId, 0);
        }


        chooseInitialPlayer();


        stage = GameStage.ATTACKING;


        Map<String, Object> stageChangeMessage = new HashMap<>();
        stageChangeMessage.put("action", "stage_change");
        stageChangeMessage.put("stage", "ATTACKING");
        broadcast(stageChangeMessage);


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        sendDetailedMapUpdate();


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        Map<String, Object> turnMessage = new HashMap<>();
        turnMessage.put("action", "player_turn");
        turnMessage.put("playerId", currentPlayerId);
        broadcast(turnMessage);

        System.out.println("Auto-fill complete. Game is now in ATTACKING stage. Current player: " + currentPlayerId);
    }

    private void sendDetailedMapUpdate() {

        List<Map<String, Object>> countriesData = new ArrayList<>();

        for (Country country : allCountrys) {
            Map<String, Object> countryData = new HashMap<>();
            countryData.put("countryId", country.getId());


            Long ownerId = null;
            int troops = 0;

            for (Map.Entry<Long, List<Occupy>> entry : occupies.entrySet()) {
                for (Occupy occupy : entry.getValue()) {
                    if (occupy.getCountryId() == country.getId()) {
                        ownerId = occupy.getPlayerId();
                        troops = occupy.getTroops();
                        break;
                    }
                }
                if (ownerId != null) break;
            }

            countryData.put("troops", troops);
            if (ownerId != null) {
                countryData.put("playerId", ownerId);
            }

            countriesData.add(countryData);
        }

         Map<String, Object> mapUpdateMessage = new HashMap<>();
        mapUpdateMessage.put("action", "map_update");
        mapUpdateMessage.put("countries", countriesData);
        broadcast(mapUpdateMessage);

        System.out.println("Sent detailed map update with " + countriesData.size() + " countries");
    }

}
