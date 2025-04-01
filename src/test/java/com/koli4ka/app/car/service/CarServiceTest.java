package com.koli4ka.app.car.service;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.model.FuelType;
import com.koli4ka.app.car.model.TransmissionType;
import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.exeption.NoCarsFoundExeption;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Car> criteriaQuery;

    @Mock
    private Root<Car> root;

    @Mock
    private TypedQuery<Car> typedQuery;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private User testUser;
    private CreateCarRequest createCarRequest;
    private SearchCarRequest searchCarRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testCar = Car.builder()
                .id(UUID.randomUUID())
                .brand("Toyota")
                .model("Camry")
                .year(2020)
                .price(25000.0)
                .transmission(TransmissionType.Автоматични)
                .fuelType(FuelType.Бензин)
                .power(200)
                .imageUrl("http://example.com/car.jpg")
                .mileage(50000)
                .location("Sofia")
                .description("Great condition")
                .publisher(testUser)
                .build();

        createCarRequest = new CreateCarRequest();
        createCarRequest.setBrand("Toyota");
        createCarRequest.setModel("Camry");
        createCarRequest.setYear(2020);
        createCarRequest.setPrice(25000.0);
        createCarRequest.setTransmission(TransmissionType.Автоматични);
        createCarRequest.setFuelType(FuelType.Бензин);
        createCarRequest.setPower(200);
        createCarRequest.setImageUrl("http://example.com/car.jpg");
        createCarRequest.setMileage(50000);
        createCarRequest.setLocation("Sofia");
        createCarRequest.setDescription("Great condition");

        searchCarRequest = new SearchCarRequest();
    }

    @Test
    void getCars_WhenNoSearchCriteria_ShouldReturnAllCars() {
        // Arrange
        List<Car> expectedCars = Arrays.asList(testCar);
        when(carRepository.findAll()).thenReturn(expectedCars);

        // Act
        List<Car> result = carService.getCars(searchCarRequest);

        // Assert
        assertEquals(expectedCars, result);
        verify(carRepository).findAll();
        verifyNoInteractions(entityManager);
    }

    @Test
    void getCars_WhenSearchCriteriaProvided_ShouldSearchCars() {
        // Arrange
        searchCarRequest.setBrand("Toyota");
        searchCarRequest.setModel("Camry");
        List<Car> expectedCars = Arrays.asList(testCar);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Car.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Car.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedCars);

        // Act
        List<Car> result = carService.getCars(searchCarRequest);

        // Assert
        assertEquals(expectedCars, result);
        verify(entityManager).getCriteriaBuilder();
        verifyNoInteractions(carRepository);
    }

    @Test
    void getCars_WhenNoCarsFound_ShouldThrowException() {
        // Arrange
        searchCarRequest.setBrand("Toyota");
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Car.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Car.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(NoCarsFoundExeption.class, () -> carService.getCars(searchCarRequest));
    }

    @Test
    void addCar_ShouldSaveCar() {
        // Act
        carService.addCar(createCarRequest, testUser);

        // Assert
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void getCar_WhenCarExists_ShouldReturnCar() {
        // Arrange
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));

        // Act
        Car result = carService.getCar(testCar.getId());

        // Assert
        assertEquals(testCar, result);
        verify(carRepository).findById(testCar.getId());
    }

    @Test
    void getCar_WhenCarDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(carRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> carService.getCar(nonExistentId));
        verify(carRepository).findById(nonExistentId);
    }
} 