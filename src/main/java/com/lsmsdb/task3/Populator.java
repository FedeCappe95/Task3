package com.lsmsdb.task3;

import java.io.*;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.neo4jmanager.Neo4JManager;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to "create" and populate the database
 */
public class Populator {

    /**
     * @param args the command line arguments
     */
    private static String personDir = "./datasetTask3/datasetTask3/people_dataset.txt" ;
    private static String placeDir = "./datasetTask3/datasetTask3/places.txt";
    private static String livingDir = "./datasetTask3/datasetTask3/living_dataset.txt";
    private static String movements = "./datasetTask3/datasetTask3/movements/";
    
    public static void loadUsers() {
        //reading of the file containing the dataset of the people
        try(BufferedReader reader = new BufferedReader(new FileReader(personDir))){
            String line = reader.readLine();
            while(line!=null) {
                String[] toInsert = line.split(",");
                Person p = new Person(toInsert[0], toInsert[2], toInsert[1]);
                Neo4JManager.getIstance().addPerson(p);
                line = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static boolean loadPlaces(String locFile) {
        //get the current number of places
        Long prevPlaces = Neo4JManager.getIstance().countPlaces();
        
        //reading of the file containing the dataset of the places
        Long counter = 0L;
        try(BufferedReader reader = new BufferedReader(new FileReader(locFile))){
            String line = reader.readLine();
            while(line!=null) {
                String[] toInsert = line.split(",");
                Place p = new Place(toInsert[0], toInsert[5], Long.parseLong(toInsert[4]), toInsert[1]);
                p.setLatitude(Double.parseDouble(toInsert[2]));
                p.setLongitude(Double.parseDouble(toInsert[3]));
                Neo4JManager.getIstance().addPlace(p);
                ++counter;
                line = reader.readLine(); 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        //check if all the places have been added
        if((Neo4JManager.getIstance().countPlaces() - prevPlaces) != counter) {
            return false;
        }
        return true;
    }
    
    public static void loadVisitedRelations() {
        //reading of the file containing the dataset of the 'visited' relations
        Long id = 0L;
        for(id = 1L; id <= 100; ++id) {
            String s = Long.toString(id);
            if(s.length() == 2){
                s = "0" + s;
            }
            else if(s.length() == 1) {
                s = "00" + s;
            }

            try(BufferedReader reader = new BufferedReader(new FileReader(movements + s))) {
                String line = reader.readLine();
                while(line!=null) {
                    String[] splitted = line.split(",");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                    Date date = sdf.parse(splitted[0]);
                    Long ts = date.getTime();
                    Neo4JManager.getIstance().visit(s, splitted[1], ts);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void loadLivesInRelations() {
        //reading of the file containing the dataset of the 'lives_in' relations
        try(BufferedReader reader = new BufferedReader(new FileReader(livingDir))){
            String line = reader.readLine();
            while(line!=null) {
                String[] splitted = line.split(",");
                String nameHouse = splitted[0];
                String idUser = splitted[1];
                Neo4JManager.getIstance().lives_in(idUser, nameHouse, System.currentTimeMillis());
                line = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if(args[0].equals("--createdb")) {
            Neo4JManager.getIstance().connect();
            loadUsers();
            boolean ret = loadPlaces(placeDir);
            loadLivesInRelations();
            loadVisitedRelations();
            if(ret){
                System.out.println("The creation of the database was successfull!!!");
            }
            else {
                System.err.println("ATTENTION: creation of database went wrong");
            }
            Neo4JManager.getIstance().close();
        }
        else if(args[0].equals("--addplaces")) {
            Neo4JManager.getIstance().connect();
            if(!loadPlaces(args[1])) {
            System.err.println("ATTENTION: insertion of new places went wrong");
            }
            else {
                System.out.println("New places insertion was successful!!!");
            }
            Neo4JManager.getIstance().close();
        }
        else {
            System.err.println("ATTENTION: typing error!\nCheck if you inserted the right command");
        }        
    }
}

