package com.koli4ka.app.user;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String NAME = "Ivan";
    private static final String EMAIL = "ivan@gmail.com";
    private static final String PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Test
    void registerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

    }

    @Test
    @WithMockUser(username = "username", roles = {"ADMIN"})
    void loginTest() throws Exception {
        AuthenticationDetails details=new AuthenticationDetails(UUID.randomUUID(),"usenrame","123", UserRole.ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(username = "username", roles = {"ADMIN"})
    void loginPageWhenAuthenticatedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }




}