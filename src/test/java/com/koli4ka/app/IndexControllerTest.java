package com.koli4ka.app;




import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.IndexController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


@AutoConfigureMockMvc
@WebMvcTest(IndexController.class)  // Тестове само за IndexController
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;



    @Test
    @WithMockUser
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/");

        //.andExpect() - проверявам резултата
        // MockMvcResultMatchers.status() - проверка на статуса
        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }


}
