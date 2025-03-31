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


    public boolean hasAnyFieldFilled() {
        return isNotEmpty(brand) || isNotEmpty(model) || isNotEmpty(fromYear) ||
                isNotEmpty(toYear) || isNotEmpty(fuelType) || transmission != null|| maxPrice > 0 ;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
