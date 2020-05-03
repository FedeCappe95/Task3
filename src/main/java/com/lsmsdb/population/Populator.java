/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmsdb.population;

import java.io.*;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager_static;

/**
 *
 * @author lore
 */
public class Populator {

    /**
     * @param args the command line arguments
     */
    private static String personDir = "C:\\Users\\susin\\Documents\\GitHub\\Task3\\datasetTask3\\datasetTask3\\people_dataset.txt" ;
    private static String placeDir = "C:\\Users\\susin\\Documents\\GitHub\\Task3\\datasetTask3\\datasetTask3\\places.txt";
    
    public static void main(String[] args) {
        
        Neo4JManager_static.init();
        
        try(BufferedReader reader = new BufferedReader(new FileReader(personDir))){
            String line = reader.readLine();
            while(line!=null) {
                String[] toInsert = line.split(",");
                Person p = new Person(toInsert[0], toInsert[1], toInsert[2]);
                Neo4JManager_static.addPerson(p);
                line = reader.readLine();
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(placeDir))){
            String line = reader.readLine();
            Long id = 0L;
            while(line!=null) {
                String[] toInsert = line.split(",");
                Place p = new Place(toInsert[0], id);
                p.setLatitude(Double.parseDouble(toInsert[2]));
                p.setLongitude(Double.parseDouble(toInsert[3]));
                Neo4JManager_static.addPlace(p);
                ++id;
                line = reader.readLine(); 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Neo4JManager_static.close();
    }
}

