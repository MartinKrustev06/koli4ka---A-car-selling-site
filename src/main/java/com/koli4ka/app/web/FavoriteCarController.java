package com.koli4ka.app.web;

import com.koli4ka.app.favoriteCar.service.FavoriteCarService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class FavoriteCarController {

    private final FavoriteCarService favoriteCarService;
    private final UserService userService;

    @Autowired
    public FavoriteCarController(FavoriteCarService favoriteCarService, UserService userService) {
        this.favoriteCarService = favoriteCarService;
        this.userService = userService;
    }

    @GetMapping("/cars/favorites")
    public ModelAndView getFavoritesPage(@AuthenticationPrincipal AuthenticationDetails details) {
        ModelAndView mav = new ModelAndView("favorites");
        User user = userService.getById(details.getUserId());
        mav.addObject("user", user);
        mav.addObject("favoriteCars", favoriteCarService.getFavoriteCars(details.getUserId()));
        return mav;
    }

    @PostMapping("/favorite/{carId}")
    public ModelAndView changeFavoriteStatus(@PathVariable UUID carId, @AuthenticationPrincipal AuthenticationDetails details) {
        System.out.println("Received favorite request for car: " + carId + " from user: " + details.getUserId());
        favoriteCarService.changeStatus(carId, details.getUserId());
        return new ModelAndView("redirect:/cars/" + carId);
    }
} 