package com.koli4ka.app.web;

import com.koli4ka.app.exeption.EmailAlreadyExists;
import com.koli4ka.app.exeption.NoCarsFoundExeption;
import com.koli4ka.app.exeption.PhoneNumberAlreadyExists;
import com.koli4ka.app.exeption.UserNameAlreadyExists;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ExeptionAdvice {




    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {



        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("404-exeption");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());



        return modelAndView;
    }

    @ExceptionHandler(NoCarsFoundExeption.class)
    public ModelAndView handleNoCarsFoundException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("no-cars-found");
        return modelAndView;
    }
    @ExceptionHandler(UserNameAlreadyExists.class)
    public String handleUsernameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, UserNameAlreadyExists exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }
    @ExceptionHandler(EmailAlreadyExists.class)
    public String handleEmailAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, EmailAlreadyExists exception) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("emailAlreadyExistMessage", message);
        return "redirect:/register";
    }
    @ExceptionHandler(PhoneNumberAlreadyExists.class)
    public String handlePhoneNumberAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, PhoneNumberAlreadyExists exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("phoneAlreadyExistMessage", message);
        return "redirect:/register";
    }




}
