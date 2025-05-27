package com.proyecto3.risk.controllers.sessioncontrollers;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.proyecto3.risk.model.entities.*;
import com.proyecto3.risk.repository.CountryRepository;
import com.proyecto3.risk.service.*;
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
    private AttackPhase attackPhase;
    private Thread gameThread;
    List<Country> allCountrys = new ArrayList<>();
    private Map<Long, List<Occupy>> occupies = new ConcurrentHashMap<>();
    private final Map<Long, Integer> troopsToPlace = new ConcurrentHashMap<>();

    private Long lastAttackSourceCountryId;
    private Long lastAttackTargetCountryId;


    private final CountryService countryService;
    private final BorderService borderService;
    private final UserService userService;
    private final ContinentService continentService;

    public enum GameState {
        WAITING, PLAYING, FINISHED
    }

    public enum GameStage {
        OCCUPATION, ATTACKING, REFORCE, BONUS
    }

    public enum AttackPhase {
        SELECTING_ATTACK,
        MOVING_TROOPS,
        FINISHED
    }


    public GameSession(String token, int maxPlayers, boolean isPublic, String gameName, Long id,
                       CountryService countryService, BorderService borderService,
                       UserService userService, ContinentService continentService) {
        this.token = token;
        this.maxPlayers = maxPlayers;
        this.isPublic = isPublic;
        this.id = id;
        this.gameName = gameName;
        this.countryService = countryService;
        this.borderService = borderService;
        this.userService = userService;
        this.continentService = continentService;

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
            System.out.println("CURRENT PLAYER SIZE: " + players.size());
            startGame();
        }

        return true;
    }

    public synchronized boolean removePlayer(Long playerId) {
        PlayerSession removed = players.remove(playerId);
        if (removed != null) {

            if (players.isEmpty()) {
                endGame();
                return true;
            }

            // Broadcast player list after player was removed
            broadcastPlayerList();

            // Notify all players someone left
            Map<String, Object> leavePlayers = new HashMap<>();
            leavePlayers.put("action", "player_left");
            leavePlayers.put("player_id", playerId);
            broadcast(leavePlayers);

            if (state == GameState.PLAYING) {
                if (playerId.equals(currentPlayerId)) {
                    nextTurn(); // Only call if it was their turn
                }

                // Check for win condition
                if (players.size() == 1 && stage == GameStage.ATTACKING) {
                    winGame();
                }
            }

            return true;
        }
        return false;
    }


    private void broadcastPlayerList() {
        List<Map<String, Object>> playerInfoList = players.values().stream()
                .map(ps -> {
                    Map<String, Object> playerInfo = new HashMap<>();
                    User user = ps.getPlayer().getUser();
                    playerInfo.put("id", user.getId());
                    playerInfo.put("username", user.getUsername());
                    playerInfo.put("avatar_url", user.getAvatar().getUrl());
                    return playerInfo;
                })
                .collect(Collectors.toList());

        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("action", "player_list");
        playerListMessage.put("players", playerInfoList);

        broadcast(playerListMessage);
    }



    private void startGame() {
        state = GameState.PLAYING;
        stage = GameStage.OCCUPATION;
        attackPhase = null;

        int initialTroops = calculateNumOfTroops(maxPlayers);

        for (Long playerId : players.keySet()) {
            troopsToPlace.put(playerId, initialTroops);

            try {
                userService.incrementGamesPlayed(playerId);
            } catch (Exception e) {
                System.err.println("Failed to increment games played for user ID " + playerId + ": " + e.getMessage());

            }

        }

        Map<String, Object> gameStartMessage = new HashMap<>();
        gameStartMessage.put("action", "game_started");
        broadcast(gameStartMessage);


         autoFillTerritories();


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
               winGame();
              //  System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
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
                    case BONUS:

                        // onEnteringBonus();
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

    private void winGame() {
        if (players.size() == 1) {
            Long winnerId = players.keySet().iterator().next();
            PlayerSession ps = players.get(winnerId);

            System.out.println(" ----- WINNER IS -----> " + winnerId);

            Map<String, Object> winMessage = new HashMap<>();
            winMessage.put("action", "win");
            winMessage.put("message", "You won the game!");
            sendToPlayer(winnerId, winMessage);

            Map<String, Object> broadcastWinMessage = new HashMap<>();
            broadcastWinMessage.put("action", "win");
            broadcastWinMessage.put("message", "Player " + winnerId + " won the game!");
            broadcast(broadcastWinMessage);

            endGame(); // Important
        }
    }



    void onEnteringBonus() {
        if (stage == GameStage.BONUS) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(" WE ENTERED THE BONUS STAGE ");

            int bonusTroops = calculateBonusTroops(currentPlayerId);
            System.out.println("BONUS ----> " + bonusTroops);

            if (bonusTroops > 0) {

                troopsToPlace.put(currentPlayerId, troopsToPlace.getOrDefault(currentPlayerId, 0) + bonusTroops);


                Map<String, Object> bonusMessage = new HashMap<>();
                bonusMessage.put("action", "bonus_to_place");
                bonusMessage.put("bonusTroops", bonusTroops);
                bonusMessage.put("totalTroopsToPlace", troopsToPlace.get(currentPlayerId));
                bonusMessage.put("playerId", currentPlayerId);
                bonusMessage.put("message", "You received " + bonusTroops + " bonus troops. Place them on your territories.");
                sendToPlayer(currentPlayerId, bonusMessage);
                broadcast(bonusMessage);

              /*  Map<String, Object> bonusCollectedMessage = new HashMap<>();
                bonusCollectedMessage.put("action", "bonus");
                bonusCollectedMessage.put("playerId", currentPlayerId);
                bonusCollectedMessage.put("bonusTroops", bonusTroops);
                broadcast(bonusCollectedMessage);*/

                // handleBonusInput(currentPlayerId,input);

            } else {
                stage = GameStage.ATTACKING;
                attackPhase = AttackPhase.SELECTING_ATTACK;
                broadcastGameStage();

                Map<String, Object> noBonusMessage = new HashMap<>();
                noBonusMessage.put("action", "no_bonus");
                noBonusMessage.put("message", "No bonus troops this turn. Proceeding to attack phase.");
                sendToPlayer(currentPlayerId, noBonusMessage);

            }

            sendMapUpdate();
        }
    }

    public void handlePlayerInput(Long playerId, JsonObject input) {
        // System.out.println("Handling player input: " + input.toString() + " from player: " + playerId);
        // System.out.println("Current game state: Stage=" + stage + ", Phase=" + attackPhase);

      //  winGame();

        if (state != GameState.PLAYING) {
            //  System.out.println("Game not in playing state");
            return;
        }

        if (!playerId.equals(currentPlayerId)) {
            // System.out.println("Not player's turn: " + playerId + " vs current: " + currentPlayerId);
            sendToPlayer(playerId, Map.of("action", "error", "message", "It's not your turn"));
            return;
        }

        if (stage == GameStage.OCCUPATION) {
            // System.out.println("Handling occupation input");
            handleOccupationInput(playerId, input);
            return;
        }

        if (stage == GameStage.BONUS) {
            handleBonusInput(playerId, input);
            return;
        }


        if (stage == GameStage.ATTACKING) {
            // System.out.println("In attacking stage with phase: " + attackPhase);
            if (attackPhase == null) {
                //  System.out.println("Attack phase was null, setting to SELECTING_ATTACK");
                attackPhase = AttackPhase.SELECTING_ATTACK;
                sendMapUpdate();
            }

            if (attackPhase == AttackPhase.SELECTING_ATTACK || attackPhase == AttackPhase.FINISHED) {
                //   System.out.println("Handling attack selection input");
                handleAttackingInput(playerId, input);
            } else if (attackPhase == AttackPhase.MOVING_TROOPS) {
                //   System.out.println("Handling attack move in");
                handleAttackMoveIn(playerId, input);
            }
        } else if (stage == GameStage.REFORCE) {
            //    System.out.println("Handling reforce input");
            handleReforceInput(playerId, input);
        }

        broadcastGameState();
    }


    private void handleBonusInput(Long playerId, JsonObject input) {
        if (!input.has("type")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'type' field"));
            return;
        }

        String type = input.get("type").getAsString();


        if (type.equals("place_troops")) {

            if (!input.has("countryId") || !input.has("troops")) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "Missing countryId or troops field"));
                return;
            }

            long countryId = input.get("countryId").getAsLong();
            int troopsToPlace = input.get("troops").getAsInt();


            if (!validateTroopPlacement(playerId, countryId, troopsToPlace)) {
                return;
            }


            placeTroopsOnTerritory(playerId, countryId, troopsToPlace);

            int remainingTroops = this.troopsToPlace.get(playerId) - troopsToPlace;
            this.troopsToPlace.put(playerId, remainingTroops);

            // Send confirmation
            Map<String, Object> placementMessage = new HashMap<>();
            placementMessage.put("action", "troops_placed");
            placementMessage.put("countryId", countryId);
            placementMessage.put("troopsPlaced", troopsToPlace);
            placementMessage.put("remainingTroops", remainingTroops);
            sendToPlayer(playerId, placementMessage);


            if (remainingTroops == 0) {

                List<Occupy> playeroccupies = occupies.get(playerId);
                if (playeroccupies.isEmpty()) {
                    sendToPlayer(playerId, Map.of("action", "lose", "message", "You Loose For Not Having Any Territories"));
                    removePlayer(playerId);
                    Map<String, Object> loosemessage = new HashMap<>();
                    loosemessage.put("action", "lose");
                    loosemessage.put("message", "player " + playerId + " loses for not having any territories left");
                    broadcast(loosemessage);
                    User u = userService.getUserById(playerId);
                    int games = u.getGames();
                    u.setGames(++games);
                    userService.updateUser(u.getId(), u);
                    nextTurn();
                } else {

                    stage = GameStage.ATTACKING;
                    attackPhase = AttackPhase.SELECTING_ATTACK;
                    broadcastGameStage();
                }

            }
            sendMapUpdate();

        } else {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Invalid type for bonus input"));
        }
    }


    private boolean validateTroopPlacement(Long playerId, long countryId, int troopsToPlace) {

        int availableTroops = this.troopsToPlace.getOrDefault(playerId, 0);
        if (availableTroops == 0) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You have no troops to place"));
            return false;
        }


        if (troopsToPlace > availableTroops) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You only have " + availableTroops + " troops to place"));
            return false;
        }

   /*
        if (troopsToPlace < 1) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You must place at least 1 troop"));
            return false;
        }*/

        // Check if player controls the territory
        List<Occupy> playerOccupies = occupies.get(playerId);
        if (playerOccupies == null) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You don't control any territories"));
            return false;
        }

        boolean controlsTerritory = playerOccupies.stream()
                .anyMatch(occupy -> occupy.getCountryId() == countryId);

        if (!controlsTerritory) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You don't control this territory"));
            return false;
        }

        return true;
    }

    private void placeTroopsOnTerritory(Long playerId, long countryId, int troopsToPlace) {
        List<Occupy> playerOccupies = occupies.get(playerId);

        for (Occupy occupy : playerOccupies) {
            if (occupy.getCountryId() == countryId) {
                occupy.setTroops(occupy.getTroops() + troopsToPlace);
                System.out.println("Player " + playerId + " placed " + troopsToPlace +
                        " troops on territory " + countryId +
                        ". New total: " + occupy.getTroops());
                break;
            }
        }
    }

    private int calculateBonusTroops(Long playerId) {
        List<Occupy> playerOccupies = occupies.get(playerId);
        if (playerOccupies == null || playerOccupies.isEmpty()) {
            return 0;
        }

        int totalBonusTroops = 0;


        List<Continent> allContinents = continentService.getAllContinents();

        for (Continent continent : allContinents) {

            if (playerControlsFullContinent(playerId, continent)) {
                totalBonusTroops += continent.getExtraTropes();
                System.out.println("Player " + playerId + " gets " + continent.getExtraTropes() +
                        " bonus troops for controlling CONTINENT -> " + continent.getName());
            }
        }

        System.out.println("REAL CONTINENT BONUS: " + totalBonusTroops);

        if (totalBonusTroops == 0) {
            System.out.println("SINCE Player " + playerId + " DOES NOT CONTROL ANY CONTINENT HE" + " gets NO bonus troops");
        } else {
            System.out.println("Player " + playerId + " gets " + totalBonusTroops +
                    " bonus troops for controlling continents");
        }


        // Minimum bonus based on territories controlled (1 troop per 3 territories, minimum 3)
        int territoryCount = playerOccupies.size();

        int territoryBonus = Math.max(3, territoryCount / 3);
        totalBonusTroops += territoryBonus;


        System.out.println("Player " + playerId + " gets " + territoryBonus +
                " bonus troops for controlling " + territoryCount + " territories");

        if (totalBonusTroops == 0) {
            System.out.println("FINAL BONUS OF PLAYER " + playerId + " IS 0 CAUSE HE DOES NOT CONTROL ANY CONTINENT OR TERRITORY");
        } else {
            System.out.println("FINAL Player " + playerId + " BONUS " + totalBonusTroops +
                    " for controlling continents and territories");
        }

        return totalBonusTroops;
    }

    private boolean playerControlsFullContinent(Long playerId, Continent continent) {

        boolean ans = false;

        System.out.println(" - - - INSIDE PLAYER CONTROLS FULL CONTINENT - - - ");
        List<Occupy> playerOccupies = occupies.get(playerId);
        if (playerOccupies == null) {
            return false;
        }

        // Get all country IDs that belong to this continent
        Set<Long> continentCountryIds = continent.getCountries().stream()
                .map(Country::getId)
                .collect(Collectors.toSet());

        for (Country country : continent.getCountries()) {
            System.out.println("Country: " + country.getName() + " ID: " + country.getId());
        }

        // Get all country IDs that the player controls
        Set<Long> playerCountryIds = playerOccupies.stream()
                .map(Occupy::getCountryId)
                .collect(Collectors.toSet());

        for (Occupy occupy : playerOccupies) {
            System.out.println("Player " + playerId + " occupies country ID: " + occupy.getCountryId());
        }

        // Check if player controls all countries in the continent
        if (playerCountryIds.containsAll(continentCountryIds)) {
            ans = true;

            //Continent player controls
            System.out.println("Player " + playerId + " controls continent: " + continent.getName());

        } else {
            System.out.println("Player " + playerId + " does NOT control continent: " + continent.getName());
        }


        return ans;
    }

    boolean checkAttackingInput(Long playerId, JsonObject input) {
        if (!input.has("type")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'type' field"));
            return false;
        }

        String type = input.get("type").getAsString();
        if (!type.equals("attack")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Invalid action type"));
            return false;
        }

        if (!input.has("countryId")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'countryId' field"));
            return false;
        }

        if (!input.has("enemyCountryId")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'enemyCountryId' field"));
            return false;
        }

        if (!input.has("troops")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'troops' field"));
            return false;
        }

        return true;
    }

    private void handleAttackingInput(Long playerId, JsonObject input) {
        System.out.println("ATTACK DEBUG - Received input: " + input.toString());
        System.out.println("ATTACK DEBUG - Current stage: " + stage + ", phase: " + attackPhase);

        if (!input.has("type")) {
            System.out.println("ERROR - Missing 'type' field");
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'type' field"));
            return;
        }

        String type = input.get("type").getAsString();
        System.out.println("Attack type: " + type);

        if (type.equals("end_attack")) {
            attackPhase = AttackPhase.FINISHED;
            stage = GameStage.REFORCE;
            sendToPlayer(playerId, Map.of("action", "info", "message", "Attack phase ended. Now entering fortify phase."));
            broadcast(Map.of("action", "stage_change", "stage", "REFORCE", "playerId", playerId));
            return;
        }

        if (type.equals("attack")) {
            System.out.println("Processing attack command");

            // Debug territory state before processing attack
            debugTerritoryState();

            if (!checkAttackingInput(playerId, input)) {
                System.out.println("Attack input validation failed");
                return;
            }

            long sourceCountryId = input.get("countryId").getAsLong();
            int attackingTroops = input.get("troops").getAsInt();
            long enemyCountryId = input.get("enemyCountryId").getAsLong();

            System.out.println("Attack details - Source: " + sourceCountryId +
                    ", Target: " + enemyCountryId +
                    ", Troops: " + attackingTroops);

            lastAttackSourceCountryId = sourceCountryId;
            lastAttackTargetCountryId = enemyCountryId;

            List<Occupy> playerOccupies = occupies.get(playerId);
            if (playerOccupies == null) {
                System.out.println("ERROR - Player has no occupied territories");
                sendToPlayer(playerId, Map.of("action", "error", "message", "You do not occupy any countries."));
                return;
            }

            System.out.println("Player occupies " + playerOccupies.size() + " territories");

            // Use proper Long equals comparison and detailed debug output
            Occupy sourceOccupy = null;
            for (Occupy occupy : playerOccupies) {
                if (occupy.getCountryId() == sourceCountryId) {
                    sourceOccupy = occupy;
                    System.out.println("Found source territory #" + sourceCountryId +
                            " with " + occupy.getTroops() + " troops");
                    break;
                }
            }

            if (sourceOccupy == null) {
                System.out.println("ERROR - Player does not occupy source country " + sourceCountryId);
                sendToPlayer(playerId, Map.of("action", "error", "message", "You do not occupy this country."));
                return;
            }

            int currentTroops = sourceOccupy.getTroops();
            System.out.println("Source country has " + currentTroops + " troops");

            System.out.println("****---------------------------------------------------****");
            System.out.println("Current troops: " + currentTroops);
            System.out.println("Attacking troops: " + attackingTroops);
            System.out.println("Troops to move: " + (currentTroops - attackingTroops));
            System.out.println("****---------------------------------------------------****");

            if (currentTroops <= attackingTroops) {
                System.out.println("ERROR - Not enough troops (need to leave 1 behind)");
                sendToPlayer(playerId, Map.of("action", "error", "message", "You must leave at least one troop behind."));
                return;
            }

            List<Border> borders = borderService.findByCountryId(sourceCountryId);
            List<Long> attackableZones = new ArrayList<>();

            for (Border border : borders) {
                long neighborId = border.getCountry1Id().equals(sourceCountryId)
                        ? border.getCountry2Id()
                        : border.getCountry1Id();

                boolean occupiedByEnemy = occupies.entrySet().stream()
                        .anyMatch(entry -> !entry.getKey().equals(playerId) &&
                                entry.getValue().stream().anyMatch(o -> o.getCountryId().equals(neighborId)));

                if (occupiedByEnemy) {
                    attackableZones.add(neighborId);
                }
            }

            if (attackableZones.isEmpty()) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "No enemies around this country to attack."));
                return;
            }

            if (!attackableZones.contains(enemyCountryId)) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "Invalid enemy country selected. Not adjacent or not enemy-owned."));
                return;
            }


            Long defenderId = null;
            Occupy targetOccupy = null;

            for (Map.Entry<Long, List<Occupy>> entry : occupies.entrySet()) {
                if (entry.getKey().equals(playerId)) continue;

                for (Occupy occupy : entry.getValue()) {
                    if (occupy.getCountryId() == enemyCountryId) {
                        defenderId = entry.getKey();
                        targetOccupy = occupy;
                        break;
                    }
                }
                if (defenderId != null) break;
            }

            if (defenderId == null || targetOccupy == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "Could not find defending player."));
                return;
            }


            enemyUnderAttackMessage(playerId, defenderId, sourceCountryId, enemyCountryId, attackingTroops, targetOccupy);
            attackingPlayerConfirmation(playerId, enemyCountryId, defenderId, attackingTroops, targetOccupy);
            broadcastAttack(playerId, defenderId, sourceCountryId, enemyCountryId, attackingTroops, targetOccupy);
            sendMapUpdate();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            int[] attackDice = attackerDiceRoll(attackingTroops);
            int[] defendDice = enemyDiceRoll(targetOccupy.getTroops());
            System.out.println("Attacker dice: " + Arrays.toString(attackDice));
            System.out.println("Defender dice: " + Arrays.toString(defendDice));


            Map<String, Object> diceRolls = new HashMap<>();
            diceRolls.put("action", "dice_rolls");
            diceRolls.put("attackerDice", attackDice);
            diceRolls.put("defenderDice", defendDice);
            broadcast(diceRolls);


            int attackerLosses = 0;
            int defenderLosses = 0;
            int comparisons = Math.min(attackDice.length, defendDice.length);

            for (int i = 0; i < comparisons; i++) {
                if (attackDice[i] > defendDice[i]) {
                    defenderLosses++;
                } else {
                    attackerLosses++;
                }
            }


            sourceOccupy.setTroops(sourceOccupy.getTroops() - attackerLosses);
            targetOccupy.setTroops(targetOccupy.getTroops() - defenderLosses);


            if (targetOccupy.getTroops() <= 0) {

                occupies.get(defenderId).removeIf(o -> o.getCountryId() == enemyCountryId);


                attackPhase = AttackPhase.MOVING_TROOPS;

                // Notify player to move troops
                Map<String, Object> moveTroopsPrompt = new HashMap<>();
                moveTroopsPrompt.put("action", "move_troops");
                moveTroopsPrompt.put("message", "Territory conquered! Move troops from source to conquered territory.");
                moveTroopsPrompt.put("sourceCountryId", sourceCountryId);
                moveTroopsPrompt.put("targetCountryId", enemyCountryId);
                moveTroopsPrompt.put("maxTroops", sourceOccupy.getTroops() - 1);
                sendToPlayer(playerId, moveTroopsPrompt);

                // Broadcast territory conquered message
                Map<String, Object> territoryConquered = new HashMap<>();
                territoryConquered.put("action", "territory_conquered");
                territoryConquered.put("attackerId", playerId);
                territoryConquered.put("defenderId", defenderId);
                territoryConquered.put("territoryId", enemyCountryId);
                broadcast(territoryConquered);
            } else {
                // Attack not successful, but can continue attacking
                Map<String, Object> attackResult = new HashMap<>();
                attackResult.put("action", "attack_result");
                attackResult.put("attackerLosses", attackerLosses);
                attackResult.put("defenderLosses", defenderLosses);
                attackResult.put("sourceCountryTroopsRemaining", sourceOccupy.getTroops());
                attackResult.put("targetCountryTroopsRemaining", targetOccupy.getTroops());
                broadcast(attackResult);
            }


            sendMapUpdate();
        }
    }

    private void handleAttackMoveIn(Long playerId, JsonObject input) {

        if (!input.has("type") || !input.get("type").getAsString().equals("move_troops")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Invalid or missing type for troop movement"));
            return;
        }

        if (!input.has("troops")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing troops count for movement"));
            return;
        }


        long sourceCountryId = lastAttackSourceCountryId;
        long targetCountryId = lastAttackTargetCountryId;
        int troopsToMove = input.get("troops").getAsInt();

        List<Occupy> playerOccupies = occupies.get(playerId);
        if (playerOccupies == null) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You do not occupy any countries."));
            return;
        }

        Occupy sourceOccupy = playerOccupies.stream()
                .filter(o -> o.getCountryId() == sourceCountryId)
                .findFirst()
                .orElse(null);

        if (sourceOccupy == null) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Source country not found."));
            return;
        }

        int currentTroops = sourceOccupy.getTroops();


        if (currentTroops - troopsToMove < 1) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You must leave at least one troop behind."));
            return;
        }

        if (troopsToMove < 1) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You must move at least one troop."));
            return;
        }


        Occupy newOccupy = new Occupy();
        newOccupy.setPlayerId(playerId);
        newOccupy.setCountryId(targetCountryId);
        newOccupy.setTroops(troopsToMove);


        playerOccupies.add(newOccupy);


        sourceOccupy.setTroops(sourceOccupy.getTroops() - troopsToMove);


        attackPhase = AttackPhase.SELECTING_ATTACK;
        broadcastGameState();

        sendToPlayer(playerId, Map.of(
                "action", "info",
                "message", "Troops moved successfully. You can continue attacking or end your attack phase."
        ));


        sendMapUpdate();
    }

    private void handleReforceInput(Long playerId, JsonObject input) {
        if (!input.has("type")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'type' field"));
            return;
        }

        String type = input.get("type").getAsString();

        if (type.equals("end_turn")) {
            stage = GameStage.BONUS;
            nextTurn();
            onEnteringBonus();
            sendMapUpdate();
            broadcastGameStage();

            return;
        }

        if (type.equals("fortify")) {
            if (!input.has("sourceCountryId") || !input.has("targetCountryId") || !input.has("troops")) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "Missing required fields for fortification"));
                return;
            }

            long sourceCountryId = input.get("sourceCountryId").getAsLong();
            long targetCountryId = input.get("targetCountryId").getAsLong();
            int troopsToMove = input.get("troops").getAsInt();

            List<Occupy> playerOccupies = occupies.get(playerId);
            if (playerOccupies == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You don't occupy any territories"));
                return;
            }


            Occupy sourceOccupy = playerOccupies.stream()
                    .filter(o -> o.getCountryId() == sourceCountryId)
                    .findFirst()
                    .orElse(null);

            if (sourceOccupy == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You don't control the source territory"));
                return;
            }


            Occupy targetOccupy = playerOccupies.stream()
                    .filter(o -> o.getCountryId() == targetCountryId)
                    .findFirst()
                    .orElse(null);

            if (targetOccupy == null) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You don't control the target territory"));
                return;
            }

            // Check if there's a valid path between source and target
            if (!isValidPath(sourceCountryId, targetCountryId, playerId)) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "No valid path between these territories"));
                return;
            }


            int currentTroops = sourceOccupy.getTroops();
            if (troopsToMove < 1) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You must move at least one troop"));
                return;
            }

            if (currentTroops <= troopsToMove) {
                sendToPlayer(playerId, Map.of("action", "error", "message", "You must leave at least one troop behind"));
                return;
            }


            sourceOccupy.setTroops(sourceOccupy.getTroops() - troopsToMove);
            targetOccupy.setTroops(targetOccupy.getTroops() + troopsToMove);


            sendToPlayer(playerId, Map.of("action", "info", "message", "Fortification complete. Your turn has ended."));


            Map<String, Object> fortificationMessage = new HashMap<>();
            fortificationMessage.put("action", "fortification");
            fortificationMessage.put("playerId", playerId);
            fortificationMessage.put("sourceCountryId", sourceCountryId);
            fortificationMessage.put("targetCountryId", targetCountryId);
            fortificationMessage.put("troops", troopsToMove);
            broadcast(fortificationMessage);


            sendMapUpdate();
            stage = GameStage.BONUS;
            nextTurn();
            onEnteringBonus();
            broadcastGameStage();

        }
    }


    private boolean isValidPath(long sourceCountryId, long targetCountryId, Long playerId) {
        // If source and target are the same, return false as it makes no sense
        if (sourceCountryId == targetCountryId) {

            return false;
        }

        // Get all territories owned by the player
        Set<Long> playerTerritories = occupies.get(playerId).stream()
                .map(Occupy::getCountryId)
                .collect(Collectors.toSet());

        // BFS to find path
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        queue.add(sourceCountryId);
        visited.add(sourceCountryId);

        while (!queue.isEmpty()) {
            Long currentCountryId = queue.poll();

            // If we reached the target, a path exists
            if (currentCountryId == targetCountryId) {
                return true;
            }

            // Get all neighboring territories
            List<Border> borders = borderService.findByCountryId(currentCountryId);

            for (Border border : borders) {
                // Get the ID of the neighboring territory
                Long neighborId = border.getCountry1Id().equals(currentCountryId)
                        ? border.getCountry2Id()
                        : border.getCountry1Id();

                // If the neighboring territory is owned by the player and not visited yet
                if (playerTerritories.contains(neighborId) && !visited.contains(neighborId)) {
                    queue.add(neighborId);
                    visited.add(neighborId);
                }
            }
        }

        // No path found
        return false;
    }


    int calculateDicenumber() {
        return new Random().nextInt(6) + 1;
    }

    int[] attackerDiceRoll(int attackingTroops) {
        int diceToRoll = Math.min(attackingTroops, 3);
        int[] attackerDice = new int[diceToRoll];
        for (int i = 0; i < diceToRoll; i++) {
            attackerDice[i] = calculateDicenumber();
        }
        Arrays.sort(attackerDice);
        reverse(attackerDice);
        return attackerDice;
    }

    int[] enemyDiceRoll(int defendingTroops) {
        int diceToRoll = Math.min(defendingTroops, 2);
        int[] defenderDice = new int[diceToRoll];
        for (int i = 0; i < diceToRoll; i++) {
            defenderDice[i] = calculateDicenumber();
        }
        Arrays.sort(defenderDice);
        reverse(defenderDice);
        return defenderDice;
    }

    private void reverse(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
    }

    private void broadcastAttack(Long playerId, Long defenderId, long sourceCountryId, long enemyCountryId, int attackingTroops, Occupy targetOccupy) {
        Map<String, Object> attackBroadcast = new HashMap<>();
        attackBroadcast.put("action", "attack_in_progress");
        attackBroadcast.put("attackerId", playerId);
        attackBroadcast.put("defenderId", defenderId);
        attackBroadcast.put("sourceCountryId", sourceCountryId);
        attackBroadcast.put("targetCountryId", enemyCountryId);
        attackBroadcast.put("attackingTroops", attackingTroops);
        attackBroadcast.put("defendingTroops", targetOccupy.getTroops());

        for (Long pid : players.keySet()) {
            if (!pid.equals(playerId) && !pid.equals(defenderId)) {
                sendToPlayer(pid, attackBroadcast);
            }
        }
    }

    private void attackingPlayerConfirmation(Long playerId, long enemyCountryId, Long defenderId, int attackingTroops, Occupy targetOccupy) {
        sendToPlayer(playerId, Map.of(
                "action", "attack_initiated",
                "targetCountryId", enemyCountryId,
                "defenderId", defenderId,
                "attackingTroops", attackingTroops,
                "defendingTroops", targetOccupy.getTroops()
        ));
    }

    private void enemyUnderAttackMessage(Long playerId, Long defenderId, long sourceCountryId, long enemyCountryId, int attackingTroops, Occupy targetOccupy) {
        sendToPlayer(defenderId, Map.of(
                "action", "territory_under_attack",
                "attackerId", playerId,
                "sourceCountryId", sourceCountryId,
                "targetCountryId", enemyCountryId,
                "attackingTroops", attackingTroops,
                "defendingTroops", targetOccupy.getTroops()
        ));
    }

    private void handleOccupationInput(Long playerId, JsonObject input) {
        if (!input.has("countryId")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'countryId' for occupation"));
            return;
        }

        long countryId = input.get("countryId").getAsLong();

        // Make sure troops field exists
        if (!input.has("troops")) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Missing 'troops' field"));
            return;
        }

        int numOfTroops = input.get("troops").getAsInt();

        // Validate troops value
        if (numOfTroops <= 0) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "You must place at least one troop"));
            return;
        }

        // Check if player has enough troops to place
        Integer remainingTroops = troopsToPlace.getOrDefault(playerId, 0);
        if (remainingTroops < numOfTroops) {
            sendToPlayer(playerId, Map.of("action", "error",
                    "message", "You only have " + remainingTroops + " troops to place"));
            return;
        }

        // Check if territory is already occupied by another player
        boolean alreadyOccupied = occupies.values().stream()
                .flatMap(List::stream)
                .anyMatch(o -> o.getCountryId() == countryId && !o.getPlayerId().equals(playerId));

        if (alreadyOccupied) {
            sendToPlayer(playerId, Map.of("action", "error", "message", "Territory already occupied by another player"));
            return;
        }

        // Check if player already owns this territory (add troops instead of creating new occupation)
        List<Occupy> playerOccupies = occupies.computeIfAbsent(playerId, k -> new ArrayList<>());
        Occupy existingOccupation = playerOccupies.stream()
                .filter(o -> o.getCountryId() == countryId)
                .findFirst()
                .orElse(null);

        if (existingOccupation != null) {
            // Player already owns this territory - add troops instead
            System.out.println("Player " + playerId + " adding " + numOfTroops +
                    " troops to existing territory #" + countryId);
            existingOccupation.setTroops(existingOccupation.getTroops() + numOfTroops);
        } else {
            // New territory occupation
            System.out.println("Player " + playerId + " occupying new territory #" +
                    countryId + " with " + numOfTroops + " troops");
            playerOccupies.add(new Occupy(playerId, countryId, numOfTroops));
        }

        // Subtract from available troops
        troopsToPlace.put(playerId, remainingTroops - numOfTroops);
        System.out.println("Player " + playerId + " has " + troopsToPlace.get(playerId) + " troops left to place");

        // Debug territory state after occupation
        debugTerritoryState();

        // Check if occupation phase is complete
        boolean allDone = troopsToPlace.values().stream().allMatch(t -> t <= 0);

        if (allDone) {
            stage = GameStage.BONUS;
            onEnteringBonus();
            System.out.println("SWITCHED TO BONUS STAGE IN THE OCCUPATION FUNCTION");
            broadcastGameStage();
            if (calculateBonusTroops(currentPlayerId) <= 0) {

                sendToPlayer(currentPlayerId, Map.of("action", "info", "message", "You have no bonus troops to place."));
                nextTurn();
            }

        } else {
            // Broadcast the territory occupation update
            Map<String, Object> occupationUpdate = new HashMap<>();
            occupationUpdate.put("action", "territory_occupied");
            occupationUpdate.put("playerId", playerId);
            occupationUpdate.put("territoryId", countryId);
            occupationUpdate.put("troops", numOfTroops);
            broadcast(occupationUpdate);

            // Update the map to show current territory state
            sendMapUpdate();

            // Move to next player's turn
            nextTurn();
        }
    }

    private void debugTerritoryState() {
        System.out.println("\n=== TERRITORY STATE INSPECTION ===");
        System.out.println("Current player ID: " + currentPlayerId);
        System.out.println("Total occupied territories in system: " +
                occupies.values().stream().mapToInt(List::size).sum());

        // Print all player territories
        for (Map.Entry<Long, List<Occupy>> entry : occupies.entrySet()) {
            Long pid = entry.getKey();
            List<Occupy> territories = entry.getValue();
            System.out.println("\nPlayer " + pid + " occupies " + territories.size() + " territories:");

            for (Occupy territory : territories) {
                System.out.println("  Territory #" + territory.getCountryId() +
                        " with " + territory.getTroops() + " troops");
            }
        }
        System.out.println("=================================\n");
    }

    private void sendMapUpdate() {
        Map<Long, Integer> countryTroopMap = new HashMap<>();
        Map<Long, Long> countryOwnerMap = new HashMap<>();


        for (Country country : allCountrys) {
            countryTroopMap.put(country.getId(), 0);
            countryOwnerMap.put(country.getId(), null);  // No owner initially
        }


        for (List<Occupy> occupyList : occupies.values()) {
            for (Occupy occupy : occupyList) {
                long countryId = occupy.getCountryId();
                int troops = occupy.getTroops();
                long playerId = occupy.getPlayerId();

                countryTroopMap.merge(countryId, troops, Integer::sum);
                countryOwnerMap.put(countryId, playerId);
            }
        }


        List<Map<String, Object>> countries = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : countryTroopMap.entrySet()) {
            Long countryId = entry.getKey();
            Map<String, Object> countryData = new HashMap<>();
            countryData.put("countryId", countryId);
            countryData.put("troops", entry.getValue());


            Long ownerId = countryOwnerMap.get(countryId);
            if (ownerId != null) {
                countryData.put("playerId", ownerId);
            }

            countries.add(countryData);
        }


        Map<String, Object> message = new HashMap<>();
        message.put("action", "map_update");
        message.put("countries", countries);

        broadcast(message);
    }

    private void broadcastGameState() {

        Map<String, Object> gameState = new HashMap<>();
        gameState.put("action", "game_state");
        gameState.put("state", state.toString());

        broadcast(gameState);
    }

    private void broadcastGameStage() {

        Map<String, Object> gameState = new HashMap<>();
        gameState.put("action", "stage_change");
        gameState.put("stage", stage.toString());

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

        //System.out.println("Assigning territories to " + playerIds.size() + " players...");


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


        stage = GameStage.BONUS;
        onEnteringBonus();
        System.out.println("SWITCHED TO BONUS STAGE IN THE AUTOFILL FUNCTION");


        broadcastGameStage();

        if (calculateBonusTroops(currentPlayerId) <= 0) {
            nextTurn();
        }

        //  System.out.println("Auto-fill complete. Game is now in ATTACKING stage. Current player: " + currentPlayerId);
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

    private void debugTerritories() {
        System.out.println("=== TERRITORY DEBUG ===");
        System.out.println("Total countries: " + allCountrys.size());


        System.out.println("--- BORDERS ---");
        for (Country country : allCountrys) {
            List<Border> borders = borderService.findByCountryId(country.getId());
            System.out.println("Country " + country.getId() + " borders: " +
                    borders.stream()
                            .map(b -> b.getCountry1Id().equals(country.getId()) ?
                                    b.getCountry2Id().toString() : b.getCountry1Id().toString())
                            .collect(Collectors.joining(", ")));
        }


        System.out.println("--- OCCUPATIONS ---");
        for (Map.Entry<Long, List<Occupy>> entry : occupies.entrySet()) {
            Long playerId = entry.getKey();
            List<Occupy> playerOccupies = entry.getValue();
            System.out.println("Player " + playerId + " occupies " + playerOccupies.size() + " territories:");
            for (Occupy occupy : playerOccupies) {
                System.out.println("  Country " + occupy.getCountryId() + " with " + occupy.getTroops() + " troops");
            }
        }
        System.out.println("======================");
    }
}
