package com.koli4ka.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class UserController {

    @GetMapping("/users/{id}/")
    public ModelAndView getProfile(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("id", id);
//        mav.setViewName("");
        return mav;



    }
}
