package com.lsmsdb.task3;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains all the configurable parameter
 */
public class Configuration {
    
    private static long VALIDITY_PERIOD = 2*7*24*60*60*1000; //2 weeks
    private static final Map<String,Integer> DISTANCE_LOOKUP_TABLE;
    
    static {
        DISTANCE_LOOKUP_TABLE = new HashMap<>();
        DISTANCE_LOOKUP_TABLE.put("Very close", 2);
        DISTANCE_LOOKUP_TABLE.put("Close", 4);
        DISTANCE_LOOKUP_TABLE.put("Medium", 6);
        DISTANCE_LOOKUP_TABLE.put("Distant", 8);
        DISTANCE_LOOKUP_TABLE.put("Very distant", 10);
    }
    
    /**
     * Return the current timestamp.
     * It is just a wrapper for System.currentTimeMillis(), but it is useful
     * for debug purpose to fake an execution in the past just modifing this
     * function.
     * @return 
     */
    public static long now() {
        return System.currentTimeMillis();
    }
    
    /**
     * Configure the parameter VALIDITY_PERIOD
     * @param value the new value for the parameter
     */
    public static void configureValidityPeriod(long value) {
        VALIDITY_PERIOD = value;
    }
    
    /**
     * Return the parameter VALIDITY_PERIOD
     * @return 
     */
    public static long getValidityPeriod() {
        return VALIDITY_PERIOD;
    }
    
    public static Map<String,Integer> getDistanceLookupTable() {
        return DISTANCE_LOOKUP_TABLE;
    }
    
}
