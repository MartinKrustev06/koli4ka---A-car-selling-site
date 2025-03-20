package com.koli4ka.app.web.dtos;

import com.koli4ka.app.car.model.TransmissionType;
import lombok.Data;

@Data
public class SearchCarRequest {


    private String brand;

    private String model;

    private String fromYear;

    private String toYear;

    private String fuelType;

    private TransmissionType transmission;

    private double maxPrice;


    public boolean areFieldsEmpty(){
        return brand == null || model == null || fromYear == null || toYear == null || fuelType == null || transmission == null;
    }
}
