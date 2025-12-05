package manager.game.core;

import lombok.Getter;
import lombok.Setter;
import manager.game.gameplay.League;
import manager.game.team.Team;

import java.time.LocalDate;

public class PlayerAttributes{

    @Getter @Setter private static LocalDate endOfContract = null;
    @Getter @Setter private static Team currentTeam = null;
    @Getter @Setter private static League currentLeague = null;

    PlayerAttributes(LocalDate endOfContract, Team currentTeam, League currentLeague){
        this.currentLeague = currentLeague;
        this.endOfContract = endOfContract;
        this.currentTeam = currentTeam;
    }
}
