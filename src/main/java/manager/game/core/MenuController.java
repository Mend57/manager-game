package manager.game.core;

import manager.game.gameplay.League;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final League league;

    public MenuController(League league) {
        this.league = league;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("today", java.time.LocalDate.now());
        model.addAttribute("nextMatch", league.getMatches().length > 0 ? league.getMatches()[0] : null);
        return "index";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {

        league.setPositions();

        model.addAttribute("leagueName", league.getName());
        model.addAttribute("matches", league.getMatches());
        model.addAttribute("teams", league.getTeams());

        return "schedule";
    }

    @GetMapping("/team")    public String team()   { return "team"; }
    @GetMapping("/training")public String training(){ return "training"; }
    @GetMapping("/market")  public String market()  { return "market"; }
    @GetMapping("/finances")public String finances(){ return "finances"; }
}