package manager.game.player;

import manager.game.team.Team;
import manager.game.core.GameUtils;

import java.time.LocalDate;

public class Goalkeeper extends Player{

    public Goalkeeper(int id, String name, int height, int weight, int agility, int passing, int technique,
                      int impulsion, double price, double salary, Team currentTeam, LocalDate birthday) {

        super(id, name, height, weight, agility, passing, impulsion, technique, price, salary, currentTeam, birthday);

    }

    @Override
    public double competence(){
        return GameUtils.normalize(jumpReach() + getAgility() + getPassing() + getTechnique(), 4 * GameUtils.getMINIMUM_ATTRIBUTES(), 4 * GameUtils.getATTRIBUTES_THRESHOLD());
    }

    @Override
    public double inGameCompetence(){
        return GameUtils.normalize(Math.random() * 3 + competence(), GameUtils.getMINIMUM_ATTRIBUTES(), 3 + GameUtils.getATTRIBUTES_THRESHOLD());
    }

//    @Override
//    protected void injuryRisk() {
//        if (canGetInjured()){
//            super.injuryRisk();
//        }
//    }
//
//    private boolean canGetInjured(){
//        List<Goalkeeper> goalkeepers = getCurrentTeam().getGoalkeepers();
//        return goalkeepers.size() > 1 && injuredGoalkeepers() < goalkeepers.size() - 1;
//    }
//
//    private int injuredGoalkeepers(){
//        int injuredGoalkeepers = 0;
//        List<Goalkeeper> goalkeepers = getCurrentTeam().getGoalkeepers();
//        for(Goalkeeper goalkeeper : goalkeepers){
//            if (goalkeeper.isInjury()){
//                injuredGoalkeepers++;
//            }
//        }
//        return injuredGoalkeepers;
//    }
}
