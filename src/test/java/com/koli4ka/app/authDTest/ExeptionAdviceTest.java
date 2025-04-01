package com.koli4ka.app.authDTest;

import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.exeption.EmailAlreadyExists;
import com.koli4ka.app.exeption.PhoneNumberAlreadyExists;
import com.koli4ka.app.exeption.UserNameAlreadyExists;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.web.ExeptionAdvice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class ExeptionAdviceTest {

    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CarRepository carRepository;

    @InjectMocks
    private ExeptionAdvice exeptionAdvice;





    @Test
    void testHandleUsernameAlreadyExist() throws Exception {
        UserNameAlreadyExists exception = new UserNameAlreadyExists("Username already exists");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String viewName = exeptionAdvice.handleUsernameAlreadyExist(null, redirectAttributes, exception);

        assertEquals("redirect:/register", viewName);
        verify(redirectAttributes).addFlashAttribute("usernameAlreadyExistMessage", exception.getMessage());
    }

    @Test
    void testHandleEmailAlreadyExist() throws Exception {
        EmailAlreadyExists exception = new EmailAlreadyExists("Email already exists");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String viewName = exeptionAdvice.handleEmailAlreadyExist(null, redirectAttributes, exception);

        assertEquals("redirect:/register", viewName);
        verify(redirectAttributes).addFlashAttribute("emailAlreadyExistMessage", exception.getMessage());
    }

    @Test
    void testHandlePhoneNumberAlreadyExist() throws Exception {
        PhoneNumberAlreadyExists exception = new PhoneNumberAlreadyExists("Phone number already exists");
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String viewName = exeptionAdvice.handlePhoneNumberAlreadyExist(null, redirectAttributes, exception);

        assertEquals("redirect:/register", viewName);
        verify(redirectAttributes).addFlashAttribute("phoneAlreadyExistMessage", exception.getMessage());
    }
}
