package com.koli4ka.app.web;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.favoriteCar.service.FavoriteCarService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;


@Controller
public class CarController {

    private final CarService carService;
    private final UserService userService;
    private final FavoriteCarService favoriteCarService;


    public CarController(CarService carService, UserService userService, FavoriteCarService favoriteCarService) {
        this.carService = carService;
        this.userService = userService;
        this.favoriteCarService = favoriteCarService;
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
    public ModelAndView searchCars(@AuthenticationPrincipal AuthenticationDetails details, SearchCarRequest searchCarRequest) {
        User user = userService.getById(details.getUserId());
        ModelAndView mav = new ModelAndView("offers");
        List<Car> cars=carService.getCars(searchCarRequest);
        mav.addObject("cars", cars);
        mav.addObject("user", user);
        return mav;

    }

    @GetMapping("/cars/new")
    public ModelAndView getNewCarPage(@AuthenticationPrincipal AuthenticationDetails details) {
        ModelAndView mav = new ModelAndView("add-car");
        User user = userService.getById(details.getUserId());
        mav.addObject("createCarRequest", new CreateCarRequest());
        mav.addObject("user", user);

        return mav;


    }

    @PostMapping("/cars/new")
    public ModelAndView addCar(@Valid CreateCarRequest createCarRequest, BindingResult bindingResult,@AuthenticationPrincipal AuthenticationDetails details) {
        User user = userService.getById(details.getUserId());
        if (bindingResult.hasErrors()) {
           return new ModelAndView("redirect:/cars/new");
        }
        carService.addCar(createCarRequest,user);
        return new ModelAndView("redirect:/cars/search");

    }



    @PostMapping("/cars/{id}")
    public ModelAndView handlePost(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details, @RequestParam(required = false) String _method) {
        if ("delete".equals(_method)) {
            User user = userService.getById(details.getUserId());
            carService.deleteCar(id, user);
            return new ModelAndView("redirect:/cars/my-cars");
        }
        return new ModelAndView("redirect:/cars/" + id);
    }

    @GetMapping("/cars/{id}")
    public ModelAndView getCar(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details) {
        Car car = carService.getCar(id);
        User user = userService.getById(details.getUserId());
        boolean isFavorited = favoriteCarService.isCarFavorited(id, user.getId());
        
        ModelAndView mav = new ModelAndView("car");
        mav.addObject("car", car);
        mav.addObject("user", user);
        mav.addObject("isFavorited", isFavorited);
        return mav;
    }

    @DeleteMapping("/cars/{id}")
    public ModelAndView deleteCar(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details) {
        User user = userService.getById(details.getUserId());
        carService.deleteCar(id, user);
        return new ModelAndView("redirect:/cars/my-cars");
    }

    @GetMapping("/my-cars")
    public ModelAndView getMyCars(@AuthenticationPrincipal AuthenticationDetails details) {
        User user = userService.getById(details.getUserId());
        ModelAndView mav = new ModelAndView("my-cars");
        mav.addObject("cars", carService.getCarsByUserId(details.getUserId()));
        mav.addObject("user", user);
        return mav;
    }

}
