package by.liudchyk.port.entity;

import java.util.Random;

/**
 * Created by Admin on 12.10.2016.
 */
public enum  Operation {
    LOAD, UNLOAD, BOTH;

    public static Operation generateOperation(){
        Random rand = new Random();
        Integer action = rand.nextInt(3);
        switch (action){
            case 0: return LOAD;
            case 1: return UNLOAD;
            case 2: return BOTH;
            default: return LOAD;
        }
    }
}
