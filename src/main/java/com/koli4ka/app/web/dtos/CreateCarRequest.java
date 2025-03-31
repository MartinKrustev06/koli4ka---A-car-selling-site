package com.koli4ka.app.web.dtos;

import com.koli4ka.app.car.model.FuelType;
import com.koli4ka.app.car.model.TransmissionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest {

    @NotNull(message = "Моля попълнете полето.")
    private String brand;

    @NotNull(message = "Моля попълнете полето.")
    private String model;

    @NotNull(message = "Моля попълнете полето.")
    private int year;

    @NotNull(message = "Моля попълнете полето.")
    private double price;

    @NotNull(message = "Моля попълнете полето.")
    private int power;

    @NotNull(message = "Моля попълнете полето.")
    private FuelType fuelType;

    @NotNull(message = "Моля попълнете полето.")
    private TransmissionType transmission;

    @NotNull(message = "Моля попълнете полето.")
    private int mileage;

    @NotNull(message = "Моля попълнете полето.")
    private String location;

    @NotNull(message = "Моля попълнете полето.")
    private String imageUrl;

    private String description;




}
