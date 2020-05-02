/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmsdb.task3.beans;

import java.util.Map;

/**
 *
 * @author Alberto-Surface
 */
public class Person {
    
    private String id;
    private String name;
    private String surname;
    private Long timestampInfected;
    private Long timestampHealed;
    

    public Person(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.timestampInfected = (long)0; 
        this.timestampHealed = (long)0;
                     
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
}
