package com.koli4ka.app.favoriteCar.repository;

import com.koli4ka.app.favoriteCar.model.FavoriteCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FavoriteCarRepository extends JpaRepository<FavoriteCar, UUID>{

}
