package com.koli4ka.app.favoriteCar.model;


import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteCar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne
    private User favoriteBy;

    @ManyToOne
    private Car favoriteCar;







}
