package com.koli4ka.app.car;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.model.FuelType;
import com.koli4ka.app.car.model.TransmissionType;
import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.exeption.NoCarsFoundExeption;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CarUnitTest {
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
    private Car testCar1;
    private Car testCar2;
    private User testUser;
    private UUID userId;

    private CreateCarRequest createCarRequest;
    private User user;

    private Car testCar3;
    private UUID carId;


    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setYear(2020);
        testCar.setPrice(20000);
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);

        testCar1 = new Car();
        testCar1.setBrand("Toyota");
        testCar1.setModel("Corolla");
        testCar1.setPrice(20000);
        testCar1.setPublisher(testUser);  // Колата е на този потребител

        testCar2 = new Car();
        testCar2.setBrand("Honda");
        testCar2.setModel("Civic");
        testCar2.setPrice(18000);
        testCar2.setPublisher(new User());
        user = new User();
        user.setId(UUID.randomUUID());

        createCarRequest = new CreateCarRequest();
        createCarRequest.setBrand("BMW");
        createCarRequest.setModel("X5");
        createCarRequest.setYear(2020);
        createCarRequest.setPrice(50000);
        createCarRequest.setTransmission(TransmissionType.Автоматични);
        createCarRequest.setFuelType(FuelType.Бензин);
        createCarRequest.setPower(300);
        createCarRequest.setImageUrl("http://image.com/car.jpg");
        createCarRequest.setMileage(20000);
        createCarRequest.setLocation("Sofia");
        createCarRequest.setDescription("Luxury SUV");
        carId = UUID.randomUUID();
        testCar3 = new Car();
        testCar3.setId(carId);
        testCar3.setBrand("Audi");
        testCar3.setModel("A6");
    }

    @Test
    void testSearchCars() {
        // Мокваме EntityManager и Criteria API
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Car.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Car.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(testCar));

        // Мокваме предикати (ако има)
        Predicate predicate = mock(Predicate.class);
        lenient().when(criteriaBuilder.equal(root.get("brand"), "Toyota")).thenReturn(predicate);
        lenient().when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);

        // Извикване на теста
        SearchCarRequest request = new SearchCarRequest();
        request.setBrand("Toyota");

        List<Car> result = carService.searchCars(request);

        // Проверки
        assertFalse(result.isEmpty());
        assertEquals("Toyota", result.get(0).getBrand());

        // Проверка дали са извикани нужните методи
        verify(criteriaQuery).from(Car.class);
        verify(entityManager).createQuery(criteriaQuery);
    }

    @Test
    void testGetCars_NoCarsFound_ThrowsException() {
        SearchCarRequest request = mock(SearchCarRequest.class);
        when(request.hasAnyFieldFilled()).thenReturn(false);

        when(carRepository.findAll()).thenReturn(List.of(testCar1)); // Само колата на потребителя

        assertThrows(NoCarsFoundExeption.class, () -> carService.getCars(request, userId));
    }
    @Test
    void testAddCar_Success() {
        // Извикване на метода
        carService.addCar(createCarRequest, user);

        // Проверка дали `save` е извикан веднъж с правилния обект
        verify(carRepository, times(1)).save(any(Car.class));
    }
    @Test
    void testGetCar_NotFound() {
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> carService.getCar(carId));

        verify(carRepository, times(1)).findById(carId);
    }
    @Test
    void testGetCar_Found() {
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar3));

        Car result = carService.getCar(carId);

        assertNotNull(result);
        assertEquals(carId, result.getId());
        assertEquals("Audi", result.getBrand());

        verify(carRepository, times(1)).findById(carId);
    }
}