package com.koli4ka.app.favoriteCar.repository;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.favoriteCar.model.FavoriteCar;
import com.koli4ka.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteCarRepository extends JpaRepository<FavoriteCar, UUID> {
    boolean existsByFavoriteByAndFavoriteCar(User user, Car car);
    void deleteByFavoriteByAndFavoriteCar(User user, Car car);
    boolean existsByFavoriteCarIdAndFavoriteById(UUID carId, UUID userId);
    FavoriteCar findByFavoriteByAndFavoriteCar(User user, Car car);
    List<FavoriteCar> findByFavoriteBy(User user);
}
