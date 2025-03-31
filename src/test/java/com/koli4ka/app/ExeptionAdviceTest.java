package com.koli4ka.app;

import com.koli4ka.app.car.repository.CarRepository;
import com.koli4ka.app.exeption.EmailAlreadyExists;
import com.koli4ka.app.exeption.NoCarsFoundExeption;
import com.koli4ka.app.exeption.PhoneNumberAlreadyExists;
import com.koli4ka.app.exeption.UserNameAlreadyExists;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.repository.UserRepository;
import com.koli4ka.app.web.ExeptionAdvice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
