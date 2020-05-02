package com.lsmsdb.task3.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Bean class that contains the User (Person) information.
 * It represents a type "Person" node.
 */
public class Person {
    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:hhhh");
    
    private String id;
    private String name;
    private String surname;
    private Long timestampInfected;
    private Long timestampHealed;
    

    public Person(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.timestampInfected = 0L; 
        this.timestampHealed = 0L;
    } 

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestampInfected() {
        return timestampInfected;
    }

    public void setTimestampInfected(Long timestampInfected) {
        this.timestampInfected = timestampInfected;
    }

    public Long getTimestampHealed() {
        return timestampHealed;
    }

    public void setTimestampHealed(Long timestampHealed) {
        this.timestampHealed = timestampHealed;
    }
    
    public Person(Map<String, Object> map){
        this.id = (String)map.get("id");
        this.name = (String)map.get("name");   
        this.surname = (String)map.get("surname");
        this.timestampInfected = (Long)map.get("timestampInfected");
        this.timestampHealed = (Long)map.get("timestampHealed");
    }
    
    
    
    /*
     * Other "getters" useful to display information
    */
    
    public String getFormattedInfectionDate() {
        return formatter.format(new Date(timestampInfected));
    }
    
    public String getFormattedHealedDate() {
        return formatter.format(new Date(timestampHealed));
    }
    
}
