package com.example.SpringMapMatching.Database;

import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;

public class Geo {
    private String type;
    private List<List<Double>> coordinates;
    //Integer H_ID;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

//    public void setH_ID(Integer H_ID){
//        this.H_ID = H_ID;
//    }
//
//    public Integer getH_ID(){
//        return H_ID;
//    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}
