package com.koli4ka.app.web;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;
    private User testSeller;
    private AuthenticationDetails authDetails;
    private AuthenticationDetails adminAuthDetails;
    private UUID userId;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sellerId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        testSeller = new User();
        testSeller.setId(sellerId);
        testSeller.setUsername("testseller");
        testSeller.setRole(UserRole.USER);

        authDetails = new AuthenticationDetails(userId, "testuser", "password", UserRole.USER);
        adminAuthDetails = new AuthenticationDetails(userId, "admin", "password", UserRole.ADMIN);
    }

    @Test
    @WithMockUser
    void getProfile_ShouldReturnUserProfileView() throws Exception {
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/users/{id}", userId)
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("user-profile-menu"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", testUser));
    }

    @Test
    @WithMockUser
    void getSellerProfile_ShouldReturnSellerProfileView() throws Exception {
        when(userService.getById(sellerId)).thenReturn(testSeller);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/users/seller/{id}", sellerId)
                .with(user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("seller-profile"))
                .andExpect(model().attributeExists("seller"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("seller", testSeller))
                .andExpect(model().attribute("user", testUser));
    }

    @Test
    void getAllUsers_WhenAdmin_ShouldReturnUsersView() throws Exception {
        List<User> users = Arrays.asList(testUser, testSeller);
        when(userService.getAllUsers()).thenReturn(users);
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/users")
                .with(user(adminAuthDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userRoleSwitch"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("user", testUser));
    }

    @Test
    void getAllUsers_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/users")
                .with(user(authDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeRole_WhenAdmin_ShouldRedirectToUsers() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/users/change-role/{id}", sellerId)
                .with(user(adminAuthDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    void changeRole_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/users/change-role/{id}", sellerId)
                .with(user(authDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void allEndpoints_WhenNotAuthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/users/seller/{id}", sellerId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/users/change-role/{id}", sellerId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
} 