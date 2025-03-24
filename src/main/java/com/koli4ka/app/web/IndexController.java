package com.koli4ka.app.web;


import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.LoginRequest;
import com.koli4ka.app.web.dtos.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import java.util.UUID;

@Controller
public class IndexController {
    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;

    }
    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }
    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("registerRequest", new RegisterRequest());
        return mav;

    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult result) {
        ModelAndView mav = new ModelAndView();
        if (result.hasErrors()) {
            mav.addObject("registerRequest", registerRequest);
            return mav;
        }
        userService.register(registerRequest);
        mav.setViewName("redirect:/login");
        return mav;
    }

    @GetMapping("/info")
    public ModelAndView getInfoPage(@AuthenticationPrincipal AuthenticationDetails details) {
        ModelAndView mav = new ModelAndView("info");
        User user= userService.getById(details.getUserId());
        mav.addObject("user", user);
        return mav;


    }
    @GetMapping("/info/career")
    public ModelAndView getCareerPage(@AuthenticationPrincipal AuthenticationDetails details) {
        ModelAndView mav = new ModelAndView("career");
        User user = userService.getById(details.getUserId());
        mav.addObject("user", user);
        return mav;
    }










}
