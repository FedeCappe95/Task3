package com.lsmsdb.task3;

import com.lsmsdb.task3.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that contains all the configurable parameter
 */
public class Configuration {
    
    /*
     * Constants
    */
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static final String CONFIGURATION_FILE_PATH = "/otherResources/configuration.json";
    private static final String UNDETERMINATED_INFECTION_RISK_STRING = "Undeterminated";
    
    /*
     * Configuration parameters
    */
    private static long VALIDITY_PERIOD = 2*7*24*60*60*1000; //2 weeks
    private static long USER_MOST_CRITICAL_PLACES_NUMBER = 5;
    private static LinkedHashMap<String,Integer> DISTANCE_LOOKUP_TABLE;
    private static Map<Long,String> INFECTION_RISK_LOOKUP_TABLE;
    
    /*
     * Static initialization
    */
    static {
        initWithDefaultValues();
        loadFromClasspath();
    }
    
    
    
    
    /*
     * Private methods
    */
    public static void initWithDefaultValues() {
        DISTANCE_LOOKUP_TABLE = new LinkedHashMap<>();
        DISTANCE_LOOKUP_TABLE.put("Very close", 2);
        DISTANCE_LOOKUP_TABLE.put("Close", 4);
        DISTANCE_LOOKUP_TABLE.put("Medium", 6);
        DISTANCE_LOOKUP_TABLE.put("Distant", 8);
        DISTANCE_LOOKUP_TABLE.put("Very distant", 10);
        
        INFECTION_RISK_LOOKUP_TABLE = new HashMap<>();
        INFECTION_RISK_LOOKUP_TABLE.put(4L, "Very high");
        INFECTION_RISK_LOOKUP_TABLE.put(8L, "High");
        INFECTION_RISK_LOOKUP_TABLE.put(12L, "Moderate");
        INFECTION_RISK_LOOKUP_TABLE.put(18L, "Low");
        INFECTION_RISK_LOOKUP_TABLE.put(Long.MAX_VALUE, "Very low");
    }
    
    
    
    
    /*
     * Access functions
    */
    
    /**
     * Load the configuration from the JSON file inside the classpath
     * specified by the private static final varibale CONFIGURATION_FILE_PATH
     */
    public static void loadFromClasspath() {
        try {
            JSONObject jsonRoot = new JSONObject(
                    Utils.readAsStringFromClasspath(CONFIGURATION_FILE_PATH)
            );
            VALIDITY_PERIOD = Long.parseLong(jsonRoot.getString("VALIDITY_PERIOD"));
            
            USER_MOST_CRITICAL_PLACES_NUMBER = Long.parseLong(jsonRoot.getString("USER_MOST_CRITICAL_PLACES_NUMBER"));
            
            LinkedHashMap<String,Integer> newDistanceLookupTable = new LinkedHashMap<>();
            jsonRoot.getJSONArray("DISTANCE_LOOKUP_TABLE").forEach((entry) -> {
                JSONObject o = (JSONObject)entry;
                newDistanceLookupTable.put(o.getString("key"), o.getInt("value"));
            });
            DISTANCE_LOOKUP_TABLE = newDistanceLookupTable;
            
            Map<Long,String> newInfectionRiskLookupTable = new HashMap<>();
            jsonRoot.getJSONArray("INFECTION_RISK_LOOKUP_TABLE").forEach((entry) -> {
                JSONObject o = (JSONObject)entry;
                String key = o.getString("key");
                newInfectionRiskLookupTable.put(
                        key.equals("MAX_VALUE") ? Long.MAX_VALUE : Long.parseLong(key),
                        o.getString("value")
                );
            });
        }
        catch(NumberFormatException | JSONException ex) {
            LOGGER.log(
                    Level.WARNING,
                    "Error loading configuration, default configuration will be used where it is not possible to procede",
                    ex
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
    
    /**
     * Return a lookup table (Map) that associates a string like "Very distant",
     * "Close", etc to an integer that represent the corresponding number of
     * hops
     * @return
     */
    public static Map<String,Integer> getDistanceLookupTable() {
        return DISTANCE_LOOKUP_TABLE;
    }

    /**
     * Return the configuration parameter USER_MOST_CRITICAL_PLACES_NUMBER
     * @return 
     */
    public static long getUserMostCriticalPlacesNumber() {
        return USER_MOST_CRITICAL_PLACES_NUMBER;
    }
    
    /**
     * Given a distance (hop count), this function return a String the represent
     * a "quantification" of that hop count. For example, considering the
     * default configuration, getInfectionRiskByHopCount(4) returns "Close".
     * @param distance
     * @return 
     */
    public static String getInfectionRiskByHopCount(Long distance) {
        if(distance == 0L)
            return UNDETERMINATED_INFECTION_RISK_STRING;
        
        List<Long> keySet = INFECTION_RISK_LOOKUP_TABLE.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
        for(Long key : keySet) {
            if(distance <= key)
                return INFECTION_RISK_LOOKUP_TABLE.get(key);
        }
        return INFECTION_RISK_LOOKUP_TABLE.get(keySet.get(keySet.size()-1));
    }
    
    /**
     * Return all the possible String distance, for example {Very close, Close 
     * Medium, etc...}
     * @return 
     */
    public static List<String> getDistances() {
        return DISTANCE_LOOKUP_TABLE.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
    }
    
}
