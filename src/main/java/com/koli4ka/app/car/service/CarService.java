package com.koli4ka.app.car.service;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final EntityManager entityManager;

    @Autowired
    public CarService(CarRepository carRepository, EntityManager entityManager) {
        this.carRepository = carRepository;
        this.entityManager = entityManager;
    }



    public List<Car> searchCars(SearchCarRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Car> query = cb.createQuery(Car.class);
        Root<Car> car = query.from(Car.class);

        List<Predicate> predicates = new ArrayList<>();

        // Добавяме динамично филтриране
        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            predicates.add(cb.equal(car.get("brand"), request.getBrand()));
        }
        if (request.getModel() != null && !request.getModel().isEmpty()) {
            predicates.add(cb.equal(car.get("model"), request.getModel()));
        }
        if (request.getFromYear() != null && !request.getFromYear().isEmpty()) {
            predicates.add(cb.greaterThanOrEqualTo(car.get("year"), request.getFromYear()));
        }
        if (request.getToYear() != null && !request.getToYear().isEmpty()) {
            predicates.add(cb.lessThanOrEqualTo(car.get("year"), request.getToYear()));
        }
        if (request.getFuelType() != null && !request.getFuelType().isEmpty()) {
            predicates.add(cb.equal(car.get("fuelType"), request.getFuelType()));
        }
        if (request.getTransmission() != null) {
            predicates.add(cb.equal(car.get("transmission"), request.getTransmission()));
        }
        if (request.getMaxPrice() != 0) {
            predicates.add(cb.lessThanOrEqualTo(car.get("price"), request.getMaxPrice()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Car> typedQuery = entityManager.createQuery(query);

        return typedQuery.getResultList();
    }


    public List<Car> getCars(SearchCarRequest searchCarRequest, UUID userId) {
        if (!searchCarRequest.hasAnyFieldFilled()) { // Ако няма попълнени полета
            return carRepository.findAll()
                    .stream()
                    .filter(car -> !car.getPublisher().getId().equals(userId)) // Филтрираме колите
                    .collect(Collectors.toList());
        }
        return searchCars(searchCarRequest)
                .stream()
                .filter(car -> !car.getPublisher().getId().equals(userId)) // Филтрираме и резултатите от търсенето
                .collect(Collectors.toList());
    }



    public void addCar(CreateCarRequest createCarRequest, User user) {
        Car car = Car.builder()
                .brand(createCarRequest.getBrand())
                .model(createCarRequest.getModel())
                .year(createCarRequest.getYear())
                .price(createCarRequest.getPrice())
                .transmission(createCarRequest.getTransmission())
                .fuelType(createCarRequest.getFuelType())
                .power(createCarRequest.getPower())
                .imageUrl(createCarRequest.getImageUrl())
                .mileage(createCarRequest.getMileage())
                .location(createCarRequest.getLocation())
                .description(createCarRequest.getDescription())
                .publisher(user)
                .build();
        carRepository.save(car);
    }

    public Car getCar(UUID id) {

        Optional<Car> byId = carRepository.findById(id);

        if (byId.isPresent()) {
            return byId.get();
        }
        return null;


    }
}
