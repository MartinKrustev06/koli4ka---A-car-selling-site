package com.koli4ka.app.favoriteCar.service;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.favoriteCar.model.FavoriteCar;
import com.koli4ka.app.favoriteCar.repository.FavoriteCarRepository;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteCarService {

    private final UserRepository userRepository;
    private final FavoriteCarRepository favoriteCarRepository;
    private final CarService carService;
    private final UserService userService;

    @Autowired
    public FavoriteCarService(FavoriteCarRepository favoriteCarRepository, 
                            UserRepository userRepository,
                            CarService carService,
                            UserService userService) {
        this.favoriteCarRepository = favoriteCarRepository;
        this.userRepository = userRepository;
        this.carService = carService;
        this.userService = userService;
    }

    public void changeStatus(UUID carId, UUID userId) {
        User user = userRepository.getById(userId);
        Car car = carService.getCar(carId);

        if (favoriteCarRepository.existsByFavoriteByAndFavoriteCar(user, car)) {
            // Remove from favorites
            FavoriteCar favoriteCar = favoriteCarRepository.findByFavoriteByAndFavoriteCar(user, car);
            favoriteCarRepository.delete(favoriteCar);
        } else {
            // Add to favorites
            FavoriteCar favoriteCar = new FavoriteCar();
            favoriteCar.setFavoriteBy(user);
            favoriteCar.setFavoriteCar(car);
            favoriteCarRepository.save(favoriteCar);
        }
    }

    public boolean isCarFavorited(UUID carId, UUID userId) {
        Car car = carService.getCar(carId);
        User user = userService.getById(userId);
        return favoriteCarRepository.existsByFavoriteByAndFavoriteCar(user, car);
    }

    public List<Car> getFavoriteCars(UUID userId) {
        User user = userService.getById(userId);
        List<FavoriteCar> favorites = favoriteCarRepository.findByFavoriteBy(user);
        return favorites.stream()
                .map(FavoriteCar::getFavoriteCar)
                .collect(Collectors.toList());
    }
}
