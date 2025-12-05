package manager.game.core;

import manager.game.gameplay.League;
import manager.game.player.Goalkeeper;
import manager.game.player.Outfield;
import manager.game.player.Player;
import manager.game.team.Team;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import manager.game.gameplay.GameCalendar;


import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
public class ManagerGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagerGameApplication.class, args);
    }

    @Bean
    public League league() {
        List<Player> players = new ArrayList<Player>();
        List<Outfield> outfielders = new ArrayList<Outfield>();
        List<Goalkeeper> goalkeepers = new ArrayList<Goalkeeper>();
        List<Team> teams = new ArrayList<Team>();

        for (int i = 0; i < 400; i++) {
            Outfield outfielder = new Outfield(i, "Player " + i, 175, 60,
                    GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20),
                    GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20),
                    GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20),
                    GameUtils.randomizeNumber(1, 20), GameUtils.randomizePosition(), 1000, 10000,
                    null, LocalDate.of(2000, 6, 15));
            players.add(outfielder);
            outfielders.add(outfielder);
        }
        for (int i = 0; i < 60; i++) {
            Goalkeeper goalkeeper = new Goalkeeper(i, "Goalkeeper " + i, 185,
                    70, GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20),
                    GameUtils.randomizeNumber(1, 20), GameUtils.randomizeNumber(1, 20), 1000, 10000, null, LocalDate.of(2000, 6, 15));
            players.add(goalkeeper);
            goalkeepers.add(goalkeeper);
        }

        for (int i = 0; i < 20; i++) {
            List<Outfield> outfielder = new ArrayList<Outfield>();
            List<Goalkeeper> goalkeeper = new ArrayList<Goalkeeper>();
            List<Player> player = new ArrayList<Player>();
            for (int j = 0; j < 20; j++) {
                outfielder.add(outfielders.get(0));
                outfielders.remove(outfielders.get(0));
            }
            for (int j = 0; j < 3; j++) {
                goalkeeper.add(goalkeepers.getFirst());
                goalkeepers.remove(goalkeepers.getFirst());
            }
            player.addAll(outfielder);
            player.addAll(goalkeeper);
            Team team = new Team(i, "Team " + i, player, 10000000,
                    100000, 1, 3);
            teams.add(team);
        }

        Map<Team, Integer> teamMap = new HashMap<>();
        teams.forEach(team -> teamMap.put(team, team.getPoints()));

        return new League(1, teamMap, 100000, GameCalendar.getStartOfSeason().getDayOfMonth(),
                GameCalendar.getStartOfSeason().getMonthValue(), 2024);
    }
}
