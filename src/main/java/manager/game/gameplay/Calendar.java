package manager.game.gameplay;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class Calendar {

    @Getter @Setter
    private static int day = 15, month = 2, year = 2024;
    private static LocalDate date = LocalDate.of(year, month, day);

    private static LocalDate startOfSeason = LocalDate.of(year, 3, 15);
    private static LocalDate endOfSeason = LocalDate.of(year, 12, 15);

    private static boolean gameDay;

    private static int pendingMonthlyEvents = 0;
    private static double pendingBiMonthlyEvents = 0;


    public static void addDays(int days) {
        LocalDate date     = LocalDate.of(year, month, day);
        LocalDate nextDate = date.plusDays(days);

        pendingMonthlyEvents += nextDate.getMonthValue() >= month ? nextDate.getMonthValue() - month : 12-month + nextDate.getMonthValue();
        pendingBiMonthlyEvents += (double)pendingMonthlyEvents / 2;

        day   = nextDate.getDayOfMonth();
        month = nextDate.getMonthValue();
        year  = nextDate.getYear();
        date = LocalDate.of(year, month, day);

        if (!date.isBefore(startOfSeason)){
            seasonalEvents();
            startOfSeason = LocalDate.of(year+1, startOfSeason.getMonthValue(), startOfSeason.getDayOfMonth());
        }
        if (!date.isBefore(endOfSeason)){
            endOfSeasonEvents();
            endOfSeason = LocalDate.of(year+1, endOfSeason.getMonthValue(), endOfSeason.getDayOfMonth());
        }
        if (pendingMonthlyEvents > 0) {
            for (int i = 0; i < pendingMonthlyEvents; i++) monthlyEvents();
            pendingMonthlyEvents = 0;
        }
        if (pendingBiMonthlyEvents >= 1) {
            for (int i = 0; i < (int)pendingBiMonthlyEvents; i++) biMonthlyEvents();
            pendingBiMonthlyEvents = pendingBiMonthlyEvents - (int)pendingBiMonthlyEvents;
        }
        dailyEvents();

    }

    static private void dailyEvents(){
        //for every player in the game
            //player.setAge();
//        for (Match match : //allLeagues.getMatches()){
//            if (match.getDate() == date){
//                gameDay = true;
//            }
//        }
    }

    static private void monthlyEvents(){

    }

    static private void biMonthlyEvents(){
//        for (Team team : allTeams) {
//            if (!team.isPlayerCurrentTeam()) {
//                team.setFormation(team.randomizeFormation());
//            }
//        }
    }

    static private void seasonalEvents(){
        //for every player in the game
//            if(player.retirementDate == null){
//                if (player.age >= 40 || (player.age >= 30 && Math.random() <= 0.2)) retirementDate = LocalDate.of(year+1, endOfSeason.getMonthValue(), endOfSeason.getDayOfMonth());
//            }
//        for(int i = 0; i < 100; i++){
//            randomize new players with 18yo and contracts until next transfer window and assign to random team in any division including none, 4 per team limit, except none
//        }
    }
    static private void endOfSeasonEvents() {
        //for every player in the game
//            if(player.retirementDate != null && !player.retirementDate.isBefore(date)){
//                //remove player from database
//                if(player.getCurrentTeam != null) player.getCurrentTeam.removePlayerFromTeam(player);
//            }
        //create new league
    }
}
