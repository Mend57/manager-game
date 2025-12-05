package manager.game.gameplay;

import lombok.Getter;
import manager.game.player.Player;
import manager.game.team.Team;
import manager.game.core.GameUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Match {
    private final Team homeTeam, awayTeam;

    private final int day, month, year;
    private final LocalDate date;

    public Match(Team home, Team away, int day, int month, int year) {
        this.homeTeam = home;
        this.awayTeam = away;
        this.day      = day;
        this.month    = month;
        this.year     = year;
        date = LocalDate.of(year, month, day);
    }

    public double getPerformance(Team team, double homeMultiplier){
        return GameUtils.normalize(team.teamCompetence(true) + Math.random() * 2, GameUtils.getMINIMUM_ATTRIBUTES(), GameUtils.getATTRIBUTES_THRESHOLD() * homeMultiplier + 2);
    }

    private boolean checkForMissingPlayer(Team team){
        for(Player player : team.getMainPlayers()){
            if(player == null){
                return true;
            }
        }
        return false;
    }

    public Team getLoser() {
        if(checkForMissingPlayer(homeTeam)) return awayTeam;
        if(checkForMissingPlayer(awayTeam)) return homeTeam;

        double homePerformance = getPerformance(homeTeam, 1.1);
        double awayPerformance = getPerformance(awayTeam, 1);
        if(homePerformance > awayPerformance + 2) return awayTeam;
        if(awayPerformance > homePerformance + 2) return homeTeam;

        double decider = Math.random();
        Team betterTeam;
        Team worstTeam;
        if(homePerformance >= awayPerformance){
            betterTeam = homeTeam;
            worstTeam  = awayTeam;
        }
        else {
            betterTeam = awayTeam;
            worstTeam  = homeTeam;
        }

        if (decider <= 0.2) return betterTeam;
        if (decider <= 0.5) return worstTeam;
        return null;
    }

    public Map<Team, Double> getOdds(){
        Map<Team, Double> odds = new HashMap<Team, Double>();
        double homeCompetence  = homeTeam.teamCompetence(false);
        double awayCompetence  = awayTeam.teamCompetence(false);
        double homeProbability = homeCompetence / (homeCompetence +  awayCompetence);
        double awayProbability = awayCompetence / (homeCompetence +  awayCompetence);

        double homeOdd = Math.round((1 / homeProbability) * 100.0) / 100.0;
        double awayOdd = Math.round((1 / awayProbability) * 100.0) / 100.0;
        odds.put(homeTeam, homeOdd);
        odds.put(awayTeam, awayOdd);

        return odds;
    }
}
