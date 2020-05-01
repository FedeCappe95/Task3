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
public class Place {
    
    public Long id;
    public String name;
    public Double infectionRisk;
    public Double longitude;
    public Double latitude;
    public String type;
    public Long area;
        
    
    public Place(String name, Long id) {
        this.id = id;
        this.name = name;
        this.infectionRisk = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.type = "type1";
        this.area = (long)100;
    }
    
     public Place(String name, Long id, Double risk) {
        this.id = id;
        this.name = name;
        this.infectionRisk = risk;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.type = "type1";
        this.area = (long)100;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInfectionRisk() {
        return infectionRisk;
    }

    public void setInfectionRisk(Double infectionRisk) {
        this.infectionRisk = infectionRisk;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }
    
    
    public Place(Map<String, Object> map){
        this.id = (Long)map.get("id");
        this.name = (String)map.get("name");
        this.infectionRisk = (Double)map.get("infectionRisk");
        this.latitude = (Double)map.get("latitude");
        this.longitude = (Double)map.get("longitude");
        this.type = (String)map.get("type");
        this.area = (Long)map.get("area");
    }
    
}
