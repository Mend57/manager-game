package manager.game.player;

import lombok.AccessLevel;
import manager.game.team.Team;
import manager.game.core.GameUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class Outfield extends Player {

    @Setter(AccessLevel.NONE)
    private int finishing, marking, dribbling, longShots, velocity, stamina;
    private int staminaDebuff = getAge() > 24 ? 24 - getAge() : 0;

    private Position       currentPosition;
    private final Position position;


    public Outfield(int id, String name, int height, int weight, int velocity, int agility, int stamina,
                    int passing, int finishing, int marking, int dribbling, int technique, int longShots,
                    int impulsion, Position position, double price, double salary, Team currentTeam, LocalDate birthday) {

        super(id, name, height, weight, agility, passing, impulsion, technique, price, salary, currentTeam, birthday);
        this.velocity  = velocity;
        this.finishing = finishing;
        this.marking   = marking;
        this.dribbling = dribbling;
        this.longShots = longShots;
        this.position  = position;
        this.stamina   = stamina;

    }

    public void addFinishing(int finishing) {
        this.finishing += finishing;
    }
    public void addMarking(int marking) {
        this.marking += marking;
    }
    public void addDribbling(int dribbling) {
        this.dribbling += dribbling;
    }
    public void addLongShots(int longShots) {
        this.longShots += longShots;
    }
    public void addStamina(int stamina) {
        this.stamina += stamina;
    }

    @Override
    protected int jumpReach(){
        int minStamina = 12;
        return GameUtils.normalize(super.jumpReach() - staminaPenalty(minStamina), GameUtils.getMINIMUM_ATTRIBUTES(), GameUtils.getATTRIBUTES_THRESHOLD());
    }

    @Override
    public void injuryRisk(){
        int minStamina = 12;
        double injuryChance = Math.random() * 20 + (double)getWeight() / 10 + staminaPenalty(minStamina);
        if (injuryChance > 20){
            enterInjury();
        }
    }

    @Override
    public double competence(){
        return switch (position) {
            case DEFENSE  -> defensiveCompetence();
            case MIDFIELD -> midfieldCompetence();
            case ATTACK   -> attackCompetence();
        };
    }

    @Override
    public double inGameCompetence(){
        double multiplier;

        switch (currentPosition) {
            case DEFENSE: {
                multiplier = position.getMultiplier().get("DEFENSE");
                break;
            }
            case MIDFIELD: {
                multiplier = position.getMultiplier().get("MIDFIELD");
                break;
            }
            case ATTACK: {
                multiplier = position.getMultiplier().get("ATTACK");
                break;
            }
            case null: multiplier = 1;
        }
        return GameUtils.normalize(Math.random() * 3 + multiplier * competence(), 0.25 * GameUtils.getMINIMUM_ATTRIBUTES(), 3 + GameUtils.getATTRIBUTES_THRESHOLD());
    }

    private int speed(){
        int fullSpeed  = GameUtils.normalize(velocity + (int)Math.round(0.3 * (21-getWeight()) + 0.7 * getHeight()), GameUtils.getMINIMUM_ATTRIBUTES() * 2, GameUtils.getATTRIBUTES_THRESHOLD() * 2);
        int minStamina = 14;
        return GameUtils.normalize(fullSpeed - staminaPenalty(minStamina), GameUtils.getMINIMUM_ATTRIBUTES(), GameUtils.getATTRIBUTES_THRESHOLD());
    }

    private int strength(){
        int fullStrength = (int)Math.round(0.3 * getHeight() + 0.7 * getWeight());
        int minStamina   = 14;
        return fullStrength - staminaPenalty(minStamina);
    }

    private int staminaPenalty(int penalty){
        return stamina + staminaDebuff < penalty ? (int)Math.round((double)(penalty - stamina) / 2) : 0;
    }

    private double defensiveCompetence(){
        return GameUtils.normalize(marking + getTechnique() + jumpReach() + strength() + getAgility() + getPassing() + speed(), 7 * GameUtils.getMINIMUM_ATTRIBUTES(), 7 * GameUtils.getATTRIBUTES_THRESHOLD());
    }

    private double midfieldCompetence(){
        return GameUtils.normalize(marking + dribbling + longShots + getTechnique() + jumpReach() + strength() + getAgility() + getPassing() + speed(), 9 * GameUtils.getMINIMUM_ATTRIBUTES(), 9 * GameUtils.getATTRIBUTES_THRESHOLD());
    }

    private double attackCompetence(){
        return GameUtils.normalize( finishing + dribbling + longShots + getTechnique() + jumpReach() + strength() + getAgility() + getPassing() + speed(), 9 * GameUtils.getMINIMUM_ATTRIBUTES(), 9 * GameUtils.getATTRIBUTES_THRESHOLD());
    }

}
