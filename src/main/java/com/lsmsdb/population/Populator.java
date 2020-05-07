/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmsdb.population;

import java.io.*;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static String livingDir = "C:\\Users\\susin\\Documents\\GitHub\\Task3\\datasetTask3\\datasetTask3\\living_dataset.txt";
    
    public static void main(String[] args) {
        
        Neo4JManager.getIstance().connect();
        
        try(BufferedReader reader = new BufferedReader(new FileReader(personDir))){
            String line = reader.readLine();
            while(line!=null) {
                String[] toInsert = line.split(",");
                Person p = new Person(toInsert[0], toInsert[1], toInsert[2]);
                Neo4JManager.getIstance().addPerson(p);
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
                Place p = new Place(id, toInsert[0], toInsert[5], toInsert[1], Long.parseLong(toInsert[4]));
                p.setLatitude(Double.parseDouble(toInsert[2]));
                p.setLongitude(Double.parseDouble(toInsert[3]));
                Neo4JManager.getIstance().addPlace(p);
                ++id;
                line = reader.readLine(); 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Map <String, Long> map = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(placeDir))){
            
            String movements = "C:\\Users\\susin\\Documents\\GitHub\\Task3\\datasetTask3\\movements\\";
            
            String line = reader.readLine();
            Long id = 0L;
            while(line!=null) {
                String[] forMap = line.split(",");
                map.put(forMap[0], id);
                ++id;
                line = reader.readLine();
            }
            for(id = 1L; id <= 100; ++id) {
                String s = Long.toString(id);
                if(s.length() > 1){
                    s = "0" + s;
                }
                else if(s.length() == 1) {
                    s = "00" + s;
                }
                BufferedReader secRead = new BufferedReader(new FileReader(movements + s));
                String secLine = secRead.readLine();
                while(secLine!=null) {
                    String[] splitted = secLine.split(",");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                    Date date = sdf.parse(splitted[0]);
                    Long ts = date.getTime();
                    Neo4JManager.getIstance().visit(s, map.get(splitted[1]), ts);
                    secLine = secRead.readLine();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(livingDir))){
            String line = reader.readLine();
            while(line!=null) {
                String[] splitted = line.split(",");
                Long idHouse = Long.parseLong(splitted[0]);
                String idUser = splitted[1];
                Neo4JManager.getIstance().lives_in(idUser, idHouse, System.currentTimeMillis());
                line = reader.readLine();
            }
            
        
        }catch(Exception e){
            e.printStackTrace();
        }
        Neo4JManager.getIstance().close();
    }
}

