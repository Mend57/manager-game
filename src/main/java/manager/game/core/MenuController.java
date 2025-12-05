package manager.game.core;

import manager.game.gameplay.League;
import manager.game.gameplay.Match;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import manager.game.gameplay.GameCalendar;

import java.time.LocalDate;

@Controller
public class MenuController {

    private final League league;

    public MenuController(League league) {
        this.league = league;
    }

    @GetMapping("/")
    public String home(Model model) {
        LocalDate today = GameCalendar.getDate();
        model.addAttribute("today", today);
        Match nextMatch = null;
        for (Match match : league.getMatches()) {
            if(!match.getDate().isBefore(today)) {
                nextMatch = match;
                break;
            }
        }
        model.addAttribute("nextMatch", nextMatch);
        if (nextMatch != null) {
            model.addAttribute("daysUntilMatch", LocalDate.from(today).datesUntil(nextMatch.getDate()).count());
        } else {
            model.addAttribute("daysUntilMatch", null);
        }
        return "index";
    }

    @PostMapping("/advance-day")
    public String advanceDay(RedirectAttributes redirectAttributes) {
        GameCalendar.addDays(1);
        redirectAttributes.addFlashAttribute("message", "Dia avançado com sucesso!");
        return "redirect:/";
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