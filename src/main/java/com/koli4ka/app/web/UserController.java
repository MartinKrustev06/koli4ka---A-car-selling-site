package com.koli4ka.app.web;

import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/")
    public ModelAndView getProfile(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView();
        User user=userService.getById(id);
        mav.addObject("user",user);
        mav.setViewName("user-profile");
        return mav;



    }
}
