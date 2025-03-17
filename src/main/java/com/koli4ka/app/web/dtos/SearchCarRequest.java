package com.koli4ka.app.web.dtos;

import lombok.Data;

@Data
public class SearchCarRequest {


    private String brand;

    private String model;

    private String fromYear;

    private String toYear;

    private String fuelType;

    private String transmissionType;

    private double maxPrice;


    public boolean areFieldsEmpty(){
        if (brand == null || model == null || fromYear == null || toYear == null || fuelType == null || transmissionType == null){
            return true;
        }
        return false;
    }
}
