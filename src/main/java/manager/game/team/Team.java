package manager.game.team;

import lombok.Getter;
import manager.game.player.Goalkeeper;
import manager.game.player.Outfield;
import manager.game.player.Player;
import manager.game.player.Position;
import manager.game.gameplay.Market;
import lombok.AccessLevel;
import lombok.Setter;
import manager.game.core.GameUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class Team implements FilterByPosition {
    @Setter(AccessLevel.NONE)
    private Player[]     mainPlayers = new Player[11], reservePlayers = new Player[7];
    private final int    id;
    private final String name;
    private List<Player> players;
    private boolean      isPlayerCurrentTeam = false;

    @Setter(AccessLevel.NONE)
    private Map<Formation, Double> formationFamiliarity = new HashMap<>();
    private Formation              formation;

    @Setter(AccessLevel.NONE)
    private double salaryBudget, transactionBudget;
    private double salaryCost;

    @Setter(AccessLevel.NONE)
    private int    goals = 0, goalsAgainst = 0, goalsBalance = 0, wins = 0, losses = 0, draws = 0, points = 0;
    private int    division;
    private double prestige;

    public Team(int id, String name, List<Player> players, double salaryBudget, double transactionBudget, int division, double prestige) {
        this.id                = id;
        this.name              = name;
        this.players           = players;
        this.salaryBudget      = salaryBudget;
        this.transactionBudget = transactionBudget;
        this.division          = division;
        this.prestige          = prestige;
        this.formation         = randomizeFormation();

        setSalaryCost();
        autoMainSquad(formation);
        for (Formation formation : Formation.values()) formationFamiliarity.put(formation, 0.0);
    }

    public void setPoints() {
        points = wins * 3 + draws;
    }
    public void setGoalsBalance() {
        this.goalsBalance = goals - goalsAgainst;
    }
    public void setSalaryCost() {
        for (Player player : players) {
            salaryCost += player.getSalary();
        }
    }
    public void setIsPlayerCurrentTeam(boolean isPlayerCurrentTeam) {
        this.isPlayerCurrentTeam = isPlayerCurrentTeam;
    }
    public void addGoals(int goals){
        this.goals += goals;
    }
    public void addGoalsAgainst(int goalsAgainst){
        this.goalsAgainst += goalsAgainst;
    }
    public void addWins(int wins){
        this.wins += wins;
    }
    public void addLosses(int losses){
        this.losses += losses;
    }
    public void addDraws(int draws){
        this.draws += draws;
    }
    public void addTransactionBudget(double value){
        this.transactionBudget += value;
    }
    public void removeTransactionBudget(double value){
        this.transactionBudget -= value;
    }
    public void addSalaryBudget(double value){
        this.salaryBudget += value;
    }
    public void removeSalaryBudget(double value){
        this.salaryBudget -= value;
    }
    public void addFormationFamiliarity(Formation formation, double value) {
        Double currentFamiliarity = formationFamiliarity.get(formation);
        formationFamiliarity.put(formation, currentFamiliarity + value);
    }

    public Formation randomizeFormation(){
        double randomNum = Math.random();
        if(randomNum < 1.0 / 3){
            return Formation.FORMATION_4_3_3;
        }
        if(randomNum < 2.0 / 3){
            return Formation.FORMATION_4_4_2;
        }
        else return Formation.FORMATION_3_5_2;
    }

    public void autoMainSquad(Formation currentFormation) {
        final int expectedDefensorsNumber = currentFormation.getFormation().get("DEFENSE") , expectedMidfieldersNumber = currentFormation.getFormation().get("MIDFIELD"),
                  expectedAttackersNumber = currentFormation.getFormation().get("ATTACK");

        Player[] mainDefensePlayers = new Player[expectedDefensorsNumber], mainMidfieldPlayers = new Player[expectedMidfieldersNumber], mainAttackPlayers = new Player[expectedAttackersNumber];

        Map<Player, Double> outfieldersCompetenceMap = new HashMap<>(), defensorsCompetenceMap = new HashMap<>(), attackersCompetenceMap = new HashMap<>(),
                            midfieldersCompetenceMap = new HashMap<>(), goalkeepersCompetenceMap = new HashMap<>();

        List<Outfield> outfielders = getOutfielders(players);

        getGoalkeepers(players).forEach(player -> goalkeepersCompetenceMap.put(player, player.competence()));
        outfielders.forEach(player -> outfieldersCompetenceMap.put(player, player.competence()));
        getPlayersInPosition(Position.DEFENSE, outfielders).forEach(player -> defensorsCompetenceMap.put(player, player.competence()));
        getPlayersInPosition(Position.MIDFIELD, outfielders).forEach(player -> midfieldersCompetenceMap.put(player, player.competence()));
        getPlayersInPosition(Position.ATTACK, outfielders).forEach(player -> attackersCompetenceMap.put(player, player.competence()));

        //Sort by competence
        Map<Player, Double> goalkeepersSorted = sortPlayersByCompetence(goalkeepersCompetenceMap), outfieldersSorted = sortPlayersByCompetence(outfieldersCompetenceMap),
                            defensorsSorted = sortPlayersByCompetence(defensorsCompetenceMap), midfieldersSorted = sortPlayersByCompetence(midfieldersCompetenceMap),
                            attackersSorted = sortPlayersByCompetence(attackersCompetenceMap);

        // Select core goalkeeper for mainPlayers and reservePlayers
        boolean hasGoalKeeper = false;
        Iterator<Player> iterator = goalkeepersSorted.keySet().iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (!hasGoalKeeper) {
                mainPlayers[0] = player;
                iterator.remove();
                goalkeepersSorted.remove(player);
                hasGoalKeeper = true;
            } else {
                reservePlayers[0] = player;
                iterator.remove();
                goalkeepersSorted.remove(player);
                break;
            }
        }

        selectMainPlayersByPosition(defensorsSorted, outfieldersSorted, expectedDefensorsNumber, mainDefensePlayers);
        selectMainPlayersByPosition(midfieldersSorted, outfieldersSorted, expectedMidfieldersNumber, mainMidfieldPlayers);
        selectMainPlayersByPosition(attackersSorted, outfieldersSorted, expectedAttackersNumber, mainAttackPlayers);

        concatenateMainPlayers(mainDefensePlayers, mainMidfieldPlayers, mainAttackPlayers);
        fillRemainingSlots(defensorsSorted, midfieldersSorted, attackersSorted, outfieldersSorted, expectedDefensorsNumber);
        selectReservePlayers(goalkeepersSorted, outfieldersSorted, defensorsSorted, midfieldersSorted, attackersSorted);
    }

    private void fillRemainingSlots(Map<Player, Double> defensorsSorted, Map<Player, Double> midfieldersSorted, Map<Player, Double> attackersSorted,
                                    Map<Player, Double> outfieldersSorted, int expectedDefensorsNumber) {

        for (int i = 1; i < mainPlayers.length; i++) {
            if (!outfieldersSorted.isEmpty()) {
                if (mainPlayers[i] == null) {
                    Outfield playerMidfield = null;
                    Outfield player = null;
                    if (!midfieldersSorted.isEmpty()) {
                        playerMidfield = (Outfield) midfieldersSorted.keySet().iterator().next();
                    }
                    player = (Outfield) outfieldersSorted.keySet().iterator().next();

                    if (playerMidfield == null) {
                        mainPlayers[i] = player;
                        outfieldersSorted.remove(player);
                        if (i > expectedDefensorsNumber) player.setCurrentPosition(Position.ATTACK);
                        else player.setCurrentPosition(Position.DEFENSE);

                        if (player.getPosition() == Position.DEFENSE) defensorsSorted.remove(player);
                        else attackersSorted.remove(player);

                    } else {
                        mainPlayers[i] = playerMidfield;
                        outfieldersSorted.remove(playerMidfield);
                        midfieldersSorted.remove(playerMidfield);
                        playerMidfield.setCurrentPosition(Position.MIDFIELD);
                    }
                }
            } else break;
        }
    }

    private void selectMainPlayersByPosition(Map<Player, Double> sortedMap, Map<Player, Double> outfieldersSorted, int expectedNumberOfPlayers, Player[] mainPlayersInPosition) {
        int index = 0;
        Iterator<Player> iterator = sortedMap.keySet().iterator();

        while (iterator.hasNext()) {
            Player outfielder = iterator.next();
            Outfield player = (Outfield) outfielder;
            if (index < expectedNumberOfPlayers) {
                mainPlayersInPosition[index++] = player;
                iterator.remove();
                sortedMap.remove(player);
                outfieldersSorted.remove(player);
                player.setCurrentPosition(player.getPosition());
            } else break;
        }
    }

    private void selectReservePlayers(Map<Player, Double> goalkeepersSorted, Map<Player, Double> outfieldersSorted, Map<Player, Double> defensorsSorted,
                                      Map<Player, Double> midfieldersSorted, Map<Player, Double> attackersSorted) {

        for (int i = 0; i < reservePlayers.length; i++ ) {
            if (!goalkeepersSorted.isEmpty() || !outfieldersSorted.isEmpty()) {
                if(reservePlayers[i] == null) {
                    Goalkeeper goalkeeper = null;
                    Outfield player       = null;

                    if (!goalkeepersSorted.isEmpty()) {
                        goalkeeper = (Goalkeeper) goalkeepersSorted.keySet().iterator().next();
                    }
                    if (!outfieldersSorted.isEmpty()) {
                        player = (Outfield) outfieldersSorted.keySet().iterator().next();
                    }

                    if (player != null) {
                        reservePlayers[i] = player;
                        outfieldersSorted.remove(player);
                        switch (player.getPosition()){
                            case DEFENSE:
                                defensorsSorted.remove(player);
                                break;
                            case MIDFIELD:
                                midfieldersSorted.remove(player);
                                break;
                            case ATTACK:
                                attackersSorted.remove(player);
                                break;
                        }

                    }
                    else if (goalkeeper != null) {
                        reservePlayers[i] = goalkeeper;
                        goalkeepersSorted.remove(goalkeeper);
                    }
                }
            } else break;
        }
    }

    private Map<Player, Double> sortPlayersByCompetence(Map<Player, Double> competenceMap){
        return competenceMap.entrySet().stream()
                .sorted(Map.Entry.<Player, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private void concatenateMainPlayers(Player[] defensePlayers, Player[] midfieldPlayers, Player[] attackPlayers) {
        for (int i = 1; i < mainPlayers.length; i++){
            if(i <= defensePlayers.length){
                mainPlayers[i] = defensePlayers[i - 1];
            }
            else if(i <= defensePlayers.length + midfieldPlayers.length){
                mainPlayers[i] = midfieldPlayers[i - 1 - defensePlayers.length];
            }
            else if(i <= defensePlayers.length + midfieldPlayers.length + attackPlayers.length){
                mainPlayers[i] = attackPlayers[i - 1 - defensePlayers.length - midfieldPlayers.length];

            }
        }
    }

    public double teamCompetence(boolean inGame) {
        double mainCompetence = 0, reserveCompetence = 0;
        int numOfMainPlayers = mainPlayers.length, numOfReservePlayers = reservePlayers.length;
        Player[] originalMainPlayers = mainPlayers, originalReservePlayers = reservePlayers;
        if(!inGame) autoMainSquad(formation);

        for (Player player : mainPlayers) {
            double playerCompetence = inGame ? player.inGameCompetence() : player.competence();

            if (player instanceof Outfield) mainCompetence += playerCompetence;
            else mainCompetence += playerCompetence / 2;
        }
        for (Player player : reservePlayers) {
            double playerCompetence = inGame ? player.inGameCompetence() : player.competence();

            if (player instanceof Outfield) mainCompetence += playerCompetence;
            else mainCompetence += playerCompetence / 2;
        }

        if(!inGame){
            mainPlayers    = originalMainPlayers;
            reservePlayers = originalReservePlayers;
        }

        return GameUtils.normalize(mainCompetence + reserveCompetence / 2,
                        (numOfReservePlayers * GameUtils.getMINIMUM_ATTRIBUTES()) / 4.0 + (numOfMainPlayers-1) * GameUtils.getMINIMUM_ATTRIBUTES() + GameUtils.getMINIMUM_ATTRIBUTES() / 2.0,
                        (numOfReservePlayers * GameUtils.getATTRIBUTES_THRESHOLD()) / 2.0 + (numOfMainPlayers-1) * GameUtils.getATTRIBUTES_THRESHOLD()) + GameUtils.getATTRIBUTES_THRESHOLD() / 2.0;
    }

//    public boolean canBuy(){
//
//    }
//
//    public boolean canSell(){
//
//    }

    private void buyPlayer(Player player) {
        Double playerPrice = Market.getPlayersForSale().get(player)[0];
        Double playerSalary = Market.getPlayersForSale().get(player)[1];

        if(salaryCost + playerSalary <= salaryBudget && transactionBudget-playerPrice >= 0){
            player.setCurrentTeam(this);
            player.setForSale(false);
            player.setPrice(playerPrice);
            player.setSalary(playerSalary);
            players.add(player);
            player.unregister();
            setSalaryCost();
            removeTransactionBudget(playerPrice);
            Market.removePlayer(player);
        }
    }

    public void sellPlayer(Player player) {
        Double playerPrice = Market.getPlayersForSale().get(player)[0];

        removePlayerFromSquad(player);
        players.remove(player);
        addTransactionBudget(playerPrice);
        setSalaryCost();
    }

    public void removePlayerFromTeam(Player player) {
        removePlayerFromSquad(player);
        players.remove(player);
        setSalaryCost();
    }

    private void removePlayerFromSquad(Player target){
        for(int i = 0; i < mainPlayers.length; i++) {
            if (mainPlayers[i] == target){
                mainPlayers[i] = null;
                return;
            }
        }
        for(int i = 0; i < reservePlayers.length; i++) {
            if (reservePlayers[i] == target){
                reservePlayers[i] = null;
                break;
            }
        }
    }
}
