package com.koli4ka.app.web;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.car.model.FuelType;
import com.koli4ka.app.car.model.TransmissionType;
import com.koli4ka.app.car.service.CarService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.CreateCarRequest;
import com.koli4ka.app.web.dtos.SearchCarRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private UserService userService;

    private Car testCar;
    private User testUser;
    private CreateCarRequest createCarRequest;
    private SearchCarRequest searchCarRequest;
    private AuthenticationDetails authDetails;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        authDetails = new AuthenticationDetails(userId, "testuser", "password", UserRole.USER);

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
    @WithMockUser
    void getSearchPage_ShouldReturnSearchCarView() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/cars/search")
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("search-car"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("searchCarRequest"));
    }

    @Test
    @WithMockUser
    void testSearchCars() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        when(carService.getCars(any(SearchCarRequest.class))).thenReturn(Arrays.asList(testCar));

        mockMvc.perform(post("/cars/search")
                .with(user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("brand", "Toyota")
                .param("model", "Camry")
                .param("year", "2020")
                .param("price", "25000.0")
                .param("transmission", "Автоматични")
                .param("fuelType", "Бензин")
                .param("power", "200")
                .param("location", "Sofia"))
                .andExpect(status().isOk())
                .andExpect(view().name("offers"))
                .andExpect(model().attributeExists("cars"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void testGetCars_notFound() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        when(carService.getCars(any(SearchCarRequest.class))).thenReturn(Arrays.asList());

        mockMvc.perform(post("/cars/search")
                .with(user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("brand", "NonExistentBrand")
                .param("model", "NonExistentModel")
                .param("year", "2020")
                .param("price", "25000.0")
                .param("transmission", "Автоматични")
                .param("fuelType", "Бензин")
                .param("power", "200")
                .param("location", "Sofia"))
                .andExpect(status().isOk())
                .andExpect(view().name("offers"))
                .andExpect(model().attributeExists("cars"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void getNewCarPage_ShouldReturnAddCarView() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/cars/new")
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-car"))
                .andExpect(model().attributeExists("createCarRequest"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void addCar_WithValidData_ShouldRedirectToSearch() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(post("/cars/new")
                .with(user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("createCarRequest", createCarRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/search"));
    }

    @Test
    @WithMockUser
    void addCar_WithInvalidData_ShouldRedirectToNewCarPage() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        
        // Create an invalid request with null required fields
        CreateCarRequest invalidRequest = new CreateCarRequest();
        // Don't set any fields to trigger @NotNull validation errors

        mockMvc.perform(post("/cars/new")
                .with(user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("createCarRequest", invalidRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/new"));
    }

    @Test
    @WithMockUser
    void getCar_ShouldReturnCarView() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);
        when(carService.getCar(testCar.getId())).thenReturn(testCar);

        mockMvc.perform(get("/cars/{id}", testCar.getId())
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("car"))
                .andExpect(model().attributeExists("car"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void deleteCar_WhenUserIsNotOwner_ShouldRedirectToSearch() throws Exception {
        // Arrange
        Car car = new Car();
        car.setId(testCar.getId());
        car.setPublisher(testUser);
        when(carService.getCar(testCar.getId())).thenReturn(car);
        doNothing().when(carService).deleteCar(testCar.getId(), testUser);

        // Act & Assert
        mockMvc.perform(delete("/cars/{id}", testCar.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/search"));
    }

    @Test
    void deleteCar_WhenCarDoesNotExist_ShouldRedirectToSearch() throws Exception {
        // Arrange
        when(carService.getCar(testCar.getId())).thenReturn(null);
        doNothing().when(carService).deleteCar(testCar.getId(), testUser);

        // Act & Assert
        mockMvc.perform(delete("/cars/{id}", testCar.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/search"));
    }

    @Test
    void deleteCar_WhenUserIsOwner_ShouldRedirectToSearch() throws Exception {
        // Arrange
        Car car = new Car();
        car.setId(testCar.getId());
        car.setPublisher(testUser);
        when(carService.getCar(testCar.getId())).thenReturn(car);
        doNothing().when(carService).deleteCar(testCar.getId(), testUser);

        // Act & Assert
        mockMvc.perform(delete("/cars/{id}", testCar.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/search"));
    }

    @Test
    void allEndpoints_WhenNotAuthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/cars/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/cars/search")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/cars/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/cars/new")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/cars/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
} 