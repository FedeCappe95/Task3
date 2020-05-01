package com.lsmsdb.task3.computation;

import java.util.Date;

/**
 * Compute the infection risk of a place
 */
public class InfectionRiskCalculator {
    
    /*
     * Configuration parameters
    */
    private static final double NON_RECENT_TIMESTAMP_WEIGHT = 2.0;
    private static final double[] RADIUS_THRESHOLDS = {10, 50, 100, 300};
    private static final double[] PATH_LOSS_EXPONENTS = {2.0d, 2.1d, 2.2d};
    private static final double RECENT_TIMESTAMP_WEIGHT = 4.0;
    private static final double MAX_AREA_WEIGHT = 4.0;
    private static double FALSE_NEGATIVE_PROBABILITY = 0.2;
    private static long RECENT_TIMESTAMP_WINDOW = 3*24*60*60*1000; // 3 days
    
    /*
     * Private data memebers
    */
    private final double area;
    private double partialInfectionRisk;
    private final long now;
    
    /**
     * Construct an instance of InfectionRiskCalculator
     * @param area the area of the place for which the infection risk is calculated
     */
    public InfectionRiskCalculator(double area) {
        this.area = area;
        partialInfectionRisk = 0;
        now = Configuration.now();
    }
    
    /**
     * Add an incoming arc (relation) to the count
     * @param isFromInfected true is the incombing arc is from an infected person
     * @param date the date of the arc
     */
    public void addIncomingArc(boolean isFromInfected, Date date) {
        addIncomingArc(isFromInfected, date.getTime());
    }
    
    /**
     * Add an incoming arc (relation) to the count
     * @param isFromInfected true is the incombing arc is from an infected person
     * @param timestamp the timestamp of the acr
     */
    public void addIncomingArc(boolean isFromInfected, long timestamp) {
        double delta;
        if(isFromInfected) {
            delta = 1;
        } else {
            delta = FALSE_NEGATIVE_PROBABILITY;
        }
        delta *= getVisitTemporalWeight(timestamp);
        partialInfectionRisk += delta;
    }
    
    /**
     * Calculate and return the infection risk
     * @return infection risk
     */
    public double getInfectionRisk() {
        if(partialInfectionRisk == 0)
            return 0;
        
        return partialInfectionRisk * getAreaWeight();
    }
    
    
    
    
    
    /*
     * Static configuration functions
    */
    
    /**
     * Configure the parameter RECENT_TIMESTAMP_WINDOW
     * @param value the new value for the parameter
     */
    public static void configureRecentTimestampWindow(long value) {
        RECENT_TIMESTAMP_WINDOW = value;
    }
    
    /**
     * Configure the parameter FALSE_NEGATIVE_PROBABILITY
     * @param value the new value for the parameter
     */
    public static void configureFalseNegativeProbability(long value) {
        FALSE_NEGATIVE_PROBABILITY = value;
    }
    
    
    
    
    
    
    
    /*
     * Other private function used internally
    */
    
    private double getVisitTemporalWeight(long timestamp) {
        if(timestamp + Configuration.getValidityPeriod() < now)
            return 0.0d;
        
        if(timestamp > now)
            return RECENT_TIMESTAMP_WEIGHT;
        
        double maximum = (timestamp + RECENT_TIMESTAMP_WINDOW > now ? RECENT_TIMESTAMP_WEIGHT : NON_RECENT_TIMESTAMP_WEIGHT);
        
        double visitTemporalWeight = normalize(
            (1.0d - ((double)(now - timestamp)) / ((double)Configuration.getValidityPeriod())),
            0.0d,1.0d,1.0d,maximum
        );
        
        //System.out.println("maximum: " + maximum + " visitTemporalWeight: " + visitTemporalWeight);
        return visitTemporalWeight;
    }
    
    private double getAreaWeight() {
        double radius = Math.sqrt(area/Math.PI);
        
        if(radius < RADIUS_THRESHOLDS[0])
            return MAX_AREA_WEIGHT;
        
        if(radius > RADIUS_THRESHOLDS[RADIUS_THRESHOLDS.length-1])
            return 1.0;
        
        
        double pathLossExponent = 0;
        for(int i = 0; i < PATH_LOSS_EXPONENTS.length; ++i) {
            if(radius < RADIUS_THRESHOLDS[i+1]) {
                pathLossExponent = PATH_LOSS_EXPONENTS[i];
                break;
            }
        }
        double y = 10000.0d / Math.pow(radius+50, pathLossExponent) + 1;
        double yMax = 10000.0d / Math.pow(RADIUS_THRESHOLDS[0]+50, PATH_LOSS_EXPONENTS[0]) + 1;
        return normalize(y,1,yMax,1,MAX_AREA_WEIGHT);
    }
    
    private double normalize(double originalValue, double oldMin, double oldMax, double newMin, double newMax) {
        return (originalValue - oldMin) / (oldMax-oldMin) * (newMax-newMin) + newMin;
    }
    
}
