package com.lsmsdb.task3;

import com.lsmsdb.task3.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * Class that contains all the configurable parameter
 */
public class Configuration {
    
    private static long VALIDITY_PERIOD = 2*7*24*60*60*1000; //2 weeks
    private static long USER_MOST_CRITICAL_PLACES_NUMBER = 5;
    
    private static final Map<String,Integer> DISTANCE_LOOKUP_TABLE;
    private static final Map<Integer,String> INFECTION_RISK_LOOKUP_TABLE;
    private static final String configurationFilePath = "/otherResources/configuration.json";
    
    static {
        DISTANCE_LOOKUP_TABLE = new HashMap<>();
        DISTANCE_LOOKUP_TABLE.put("Very close", 2);
        DISTANCE_LOOKUP_TABLE.put("Close", 4);
        DISTANCE_LOOKUP_TABLE.put("Medium", 6);
        DISTANCE_LOOKUP_TABLE.put("Distant", 8);
        DISTANCE_LOOKUP_TABLE.put("Very distant", 10);
    }
    
    static {
        INFECTION_RISK_LOOKUP_TABLE = new HashMap<>();
        INFECTION_RISK_LOOKUP_TABLE.put(3, "Very high");
        INFECTION_RISK_LOOKUP_TABLE.put(7, "High");
        INFECTION_RISK_LOOKUP_TABLE.put(12, "Moderate");
        INFECTION_RISK_LOOKUP_TABLE.put(18, "Low");
        INFECTION_RISK_LOOKUP_TABLE.put(20, "Very Low");
    }
    
    public static void loadFromClasspath() {
        try {
            JSONObject jsonObject = new JSONObject(
                    Utils.readAsStringFromClasspath(configurationFilePath)
            );
            VALIDITY_PERIOD = Long.parseLong(jsonObject.getString("VALIDITY_PERIOD"));
            USER_MOST_CRITICAL_PLACES_NUMBER = Long.parseLong(jsonObject.getString("USER_MOST_CRITICAL_PLACES_NUMBER"));
        }
        catch(Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(
                    Level.WARNING,
                    "Error loading configuration, default configuration will be used"
            );
        }
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
    
    public static Map<Integer, String> getInfectionRiskLookupTable() {
        return INFECTION_RISK_LOOKUP_TABLE;
    }

    public static long getUserMostCriticalPlacesNumber() {
        return USER_MOST_CRITICAL_PLACES_NUMBER;
    }
    
}
