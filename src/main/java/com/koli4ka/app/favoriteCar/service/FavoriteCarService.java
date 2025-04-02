package com.koli4ka.app.favoriteCar.service;

import com.koli4ka.app.favoriteCar.repository.FavoriteCarRepository;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FavoriteCarService {


    private final UserRepository userRepository;
    private FavoriteCarRepository favoriteCarRepository;

    @Autowired
    public FavoriteCarService(FavoriteCarRepository favoriteCarRepository, UserRepository userRepository) {
        this.favoriteCarRepository = favoriteCarRepository;
        this.userRepository = userRepository;
    }



    public void changeStatus(UUID carId, UUID userId) {
        User user   = userRepository.getById(userId);

    }
}
