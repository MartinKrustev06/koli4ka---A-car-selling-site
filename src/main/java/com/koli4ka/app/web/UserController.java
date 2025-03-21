package com.koli4ka.app.web;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/users/{id}")
    public ModelAndView getProfile(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView();
        User user=userService.getById(id);
        mav.addObject("user",user);
        mav.setViewName("user-profile-menu");
        return mav;
    }


    @GetMapping("/users/seller/{id}")
    public ModelAndView getSellerProfile(@PathVariable UUID id,@AuthenticationPrincipal AuthenticationDetails details) {
        User seller=userService.getById(id);
        User user=userService.getById(details.getUserId());
        ModelAndView mav = new ModelAndView();
        mav.addObject("seller",seller);
        mav.addObject("user",user);
        mav.setViewName("seller-profile");
        return mav;

    }


}
