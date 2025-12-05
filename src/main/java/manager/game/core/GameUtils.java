package manager.game.core;

import lombok.Getter;
import manager.game.player.Position;

public class GameUtils {

    @Getter
    private static final int ATTRIBUTES_THRESHOLD = 20, MINIMUM_ATTRIBUTES = 1;

    public static int normalize(int number, int start, int limit){
        double normalized = MINIMUM_ATTRIBUTES + ((double)number - start) / (limit - start) * (ATTRIBUTES_THRESHOLD - MINIMUM_ATTRIBUTES);
        if(normalized >= limit) return limit;
        if (normalized <= MINIMUM_ATTRIBUTES) return MINIMUM_ATTRIBUTES;
        return (int)Math.round(normalized);
    }

    public static double normalize(double number, double start, double limit){
        double normalized = MINIMUM_ATTRIBUTES + (number - start) / (limit - start) * (ATTRIBUTES_THRESHOLD - MINIMUM_ATTRIBUTES);
        if(normalized >= limit) return limit;
        if (normalized <= MINIMUM_ATTRIBUTES) return MINIMUM_ATTRIBUTES;
        return normalized;
    }

    public static int randomizeNumber(int min, int max){
        return min + (int)Math.round(Math.random() * (max - min));
    }

    public static Position randomizePosition(){
        double randomNum = Math.random();
        if(randomNum < 0.33){
            return Position.ATTACK;
        }
        if(randomNum < 0.66){
            return Position.DEFENSE;
        }
        else return Position.MIDFIELD;
    }

}
