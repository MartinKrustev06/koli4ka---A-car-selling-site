package com.koli4ka.app.car;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.CarController;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarControllerUnitTest {

    @InjectMocks
    private CarController carController;

    @Mock
    private CarService carService;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Car> criteriaQuery;

    @Mock
    private Root<Car> carRoot;

    @Mock
    private TypedQuery<Car> typedQuery;

    @Mock
    private AuthenticationDetails authenticationDetails;

    private User user;



    @Test
    void testSearchCars() {
        // Подготвяме тестови данни
        SearchCarRequest request = new SearchCarRequest();
        request.setBrand("Audi");

        Car audiCar = new Car();
        audiCar.setBrand("Audi");

        List<Car> cars = List.of(audiCar);

        // Мокираме поведението на методите
        when(carService.searchCars(request)).thenReturn(cars);

        // Извикваме метода
        List<Car> result = carService.searchCars(request);

        // Проверяваме резултата
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Audi", result.get(0).getBrand());
    }


    @Test
    void testGetNewCarPage() {
        when(authenticationDetails.getUserId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);

        ModelAndView modelAndView = carController.getNewCarPage(authenticationDetails);

        assertEquals("add-car", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
    }

    @Test
    void testAddCar() {
        when(authenticationDetails.getUserId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

//        ModelAndView modelAndView = carController.addCar(createCarRequest, bindingResult, authenticationDetails);
        assertEquals("redirect:/cars/search", modelAndView.getViewName());
    }

    @Test
    void testGetCar() {
        UUID carId = UUID.randomUUID();
        Car car = new Car();
        car.setId(carId);

        when(carService.getCar(carId)).thenReturn(car);
        when(authenticationDetails.getUserId()).thenReturn(user.getId());
        when(userService.getById(user.getId())).thenReturn(user);

        ModelAndView modelAndView = carController.getCar(carId, authenticationDetails);

        assertEquals("car", modelAndView.getViewName());
        assertEquals(car, modelAndView.getModel().get("car"));
        assertEquals(user, modelAndView.getModel().get("user"));
    }

    @Test
    void testSearchCar() {
        SearchCarRequest request = new SearchCarRequest();
        request.setBrand("Audi");

        Car audiCar = new Car();
        audiCar.setBrand("Audi");

        when(carService.searchCars(any())).thenReturn(List.of(audiCar));

        List<Car> result = carService.searchCars(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Audi", result.get(0).getBrand());
    }

    @Test
    void testSearchCars_ReturnsEmptyList_WhenNoCarsMatch() {
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        CarService carService = new CarService(null, entityManager);
        SearchCarRequest request = new SearchCarRequest();
        request.setBrand("NonExistingBrand");

        List<Car> result = carService.searchCars(request);

        assertTrue(result.isEmpty());
    }
}
