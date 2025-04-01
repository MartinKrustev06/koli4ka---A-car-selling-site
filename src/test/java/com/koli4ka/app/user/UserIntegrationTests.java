package com.koli4ka.app.user;

import com.koli4ka.app.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTests {
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

//    @Test
//    void registerAndSaveInDataBaseInvalidPasswordTest() throws Exception {
//        Assertions.assertEquals(0, userRepository.count());
//        mockMvc.perform(MockMvcRequestBuilders.post("/register")
//                        .with(csrf())
//                        .param("name", NAME)
//                        .param("email", EMAIL)
//                        .param("password", PASSWORD))
//                .andExpect(status().isOk())
//                .andExpect(view().name("register"));
//
//    }
    @Test
    void loginTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

}