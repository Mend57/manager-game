package manager.game.player;

import manager.game.gameplay.Calendar;
import manager.game.team.Team;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import manager.game.core.GameUtils;

import java.time.LocalDate;

@Getter @Setter
public abstract class Player {
    private final int       id;
    private final String    name;
    private final int       height, weight;
    private int             age;
    private final LocalDate birthday;

    @Setter(AccessLevel.NONE) private boolean registered = false;
    private Team currentTeam;

    @Setter(AccessLevel.NONE)
    private int agility, passing, impulsion, technique;

    private double  price, salary;
    private boolean forSale;
    private LocalDate retirementDate;


    @Setter(AccessLevel.NONE) private int     injuryTime;
    @Setter(AccessLevel.NONE) private boolean injury = false;

    public Player(int id, String name, int height, int weight, int agility, int passing, int impulsion,
                  int technique, double price, double salary, Team currentTeam, LocalDate birthday) {

        final int minHeight = 150, maxHeight = 200, minWeight = 50, maxWeight = 90;

        this.id          = id;
        this.name        = name;
        this.height      = GameUtils.normalize(height, minHeight, maxHeight);
        this.weight      = GameUtils.normalize(weight, minWeight, maxWeight);
        this.agility     = agility;
        this.passing     = passing;
        this.impulsion   = impulsion;
        this.technique   = technique;
        this.price       = price;
        this.salary      = salary;
        this.currentTeam = currentTeam;
        this.forSale     = (this.currentTeam == null);
        this.birthday    = birthday;

        setAge();
    }

    public abstract double inGameCompetence();
    public abstract double competence();

    public void addAgility(int agility) {
        this.agility += agility;
    }
    public void addPassing(int passing) {
        this.passing += passing;
    }
    public void addImpulsion(int impulsion) {
        this.impulsion += impulsion;
    }
    public void addTechnique(int technique) {
        this.technique += technique;
    }
    public void register(){
        registered = true;
    }
    public void unregister(){
        registered = false;
    }
    public void setAge(){
        int yearsDifference = Calendar.getYear() - birthday.getYear();
        age = Calendar.getDay() >= birthday.getDayOfMonth() ? yearsDifference : yearsDifference - 1;
    }
    public void estimatePrice(){
        if(!isForSale()) {
            //fazer a estimativa
            //this.price = estimativa;
        }
    }

    public void enterInjury(){
        this.injury = true;
        injuryTime  = injuryGravity();
    }
    public void exitInjury(){
        injury = false;
    }

    protected int jumpReach(){
        return GameUtils.normalize(impulsion + (int)Math.round(0.3 * (21-weight) + 0.7 * height), GameUtils.getMINIMUM_ATTRIBUTES() * 2, GameUtils.getATTRIBUTES_THRESHOLD() * 2);
    }

    protected void injuryRisk(){
        double injuryChance = Math.random() * 20 + (double)weight / 10;
        if (injuryChance > 20){
            enterInjury();
        }
    }

    protected int injuryGravity(){
        double gravity = Math.random() * 20;
        if(gravity <= 13){
            return (int)Math.round(1 + Math.random() * 4);
        }
        else if(gravity <= 16){
            return (int)Math.round(7 + Math.random() * 7);
        }
        else if(gravity <= 18){
            return (int)Math.round(7 + Math.random() * 14);
        }
        else return (int)Math.round(28 + Math.random() * 28);
    }
}


