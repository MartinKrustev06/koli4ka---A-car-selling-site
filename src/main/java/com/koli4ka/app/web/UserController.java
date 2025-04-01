package com.koli4ka.app.web;

import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.web.dtos.EditUserDTO;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.UserRoleSwitch;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public String getUserProfile(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details, Model model) {
        User user = userService.getById(id);
        User currentUser = userService.getById(details.getUserId());
        
        model.addAttribute("user", user);
        model.addAttribute("currentUserId", currentUser.getId());
        return "user-profile-menu";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        User user = userService.getById(id);
        EditUserDTO editUserDTO = EditUserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .build();
        model.addAttribute("editUserDTO", editUserDTO);
        model.addAttribute("user", user);
        return "user/edit-user";
    }

    @PostMapping("/users/{id}/edit")
    public String editProfile(@PathVariable UUID id,
                            @Valid @ModelAttribute("editUserDTO") EditUserDTO editUserDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            return "user/edit-user";
        }

        try {
            userService.updateProfile(id, editUserDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Профилът е обновен успешно!");
            return "redirect:/cars/search";
        } catch (Exception e) {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "user/edit-user";
        }
    }

    @GetMapping("/users/seller/{id}")
    public ModelAndView getSellerProfile(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details) {
        User seller = userService.getById(id);
        User user = userService.getById(details.getUserId());
        
        // If the user is viewing their own profile, redirect to user-profile-menu
        if (seller.getId().equals(user.getId())) {
            return new ModelAndView("redirect:/users/" + user.getId());
        }
        
        ModelAndView mav = new ModelAndView("seller-profile");
        mav.addObject("seller", seller);
        mav.addObject("user", user);
        return mav;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationDetails details) {
        List<User> users = userService.getAllUsers();
        User user = userService.getById(details.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);
        modelAndView.addObject("user", user);
        modelAndView.addObject("userRoleSwitch", new UserRoleSwitch());

        return modelAndView;
    }

    @GetMapping("/users/change-role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView changeRole(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails details) {
        userService.changeRole(id);
        return new ModelAndView("redirect:/users");
    }
}
