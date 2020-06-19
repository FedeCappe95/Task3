package com.lsmsdb.task3;

import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServerUpdater {
    
    private final static Logger LOGGER = Logger.getLogger(MainServerUpdater.class.getName());
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println(
                "##################\n" +
                "# Server updater #\n" +
                "##################\n\n"
        );
        
        System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            "[%1$tF %1$tT %1$tL] %2$s %4$s: %5$s%6$s%n"
        );
        
        Neo4JManager neo4jmanager = Neo4JManager.getIstance();
        neo4jmanager.connect();
        
        System.out.println("");
        
        while(true) {
            LOGGER.log(Level.INFO,"Starting update");
            neo4jmanager.updateRisksOfInfectionForPlaces();
            LOGGER.log(
                    Level.INFO,
                    "Update complete, next update scheduled for {0}",
                    (new Date(Configuration.now()+Configuration.getServerUpdaterInterval())).toString()
            );
            Thread.sleep(Configuration.getServerUpdaterInterval());
        }
    }
    
}
