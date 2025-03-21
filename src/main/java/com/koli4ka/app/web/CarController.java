package com.koli4ka.app.web;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


@Controller
public class CarController {

    private final CarService carService;
    private final UserService userService;


    public CarController(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    @GetMapping("/cars/search")
    public ModelAndView getSearchPage(@AuthenticationPrincipal AuthenticationDetails details) {
        ModelAndView mav = new ModelAndView("search-car");
        User user = userService.getById(details.getUserId());
        mav.addObject("user", user);
        mav.addObject("searchCarRequest", new SearchCarRequest());
        return mav;
    }


    @PostMapping("/cars/search")
    public ModelAndView searchCars(@AuthenticationPrincipal AuthenticationDetails details, SearchCarRequest searchCarRequest, BindingResult bindingResult) {
        User user = userService.getById(details.getUserId());
        ModelAndView mav = new ModelAndView("offers");
        List<Car> cars = carService.getCars(searchCarRequest);
        mav.addObject("cars", cars);
        mav.addObject("user", user);
        return mav;

    }

    @GetMapping("/cars/new")
    public ModelAndView getNewCarPage(@AuthenticationPrincipal User user) {
        ModelAndView mav = new ModelAndView("add-car");
        mav.addObject("createCarRequest", new CreateCarRequest());
        mav.addObject("user", user);

        return mav;


    }

    @PostMapping("/cars/new")
    public ModelAndView addCar(@Valid CreateCarRequest createCarRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails details) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/cars/new");
        }
        User user = userService.getById(details.getUserId());
        carService.addCar(createCarRequest, user);
        ModelAndView mav = new ModelAndView("redirect:/cars/search");
        return mav;
    }

    @GetMapping("/cars/{id}")
    public ModelAndView getCar(@PathVariable UUID id) {

        Car car = carService.getCar(id);

        ModelAndView mav = new ModelAndView("car");
        mav.addObject("car", car);
        return mav;


    }


}

