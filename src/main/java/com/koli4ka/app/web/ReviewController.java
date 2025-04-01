package com.koli4ka.app.web;

import com.koli4ka.app.review.model.Review;
import com.koli4ka.app.review.service.ReviewService;
import com.koli4ka.app.web.dtos.CreateReviewDto;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/my-reviews")
    public ModelAndView getMyReviews(@AuthenticationPrincipal AuthenticationDetails details) {
        User user = userService.getById(details.getUserId());
        List<Review> userReviews = reviewService.getReviewsForUser(user.getId());
        
        ModelAndView mav = new ModelAndView("review/my-reviews");
        mav.addObject("user", user);
        mav.addObject("reviews", userReviews);
        return mav;
    }

    @GetMapping("/{userId}")
    public ModelAndView getUserReviews(@PathVariable UUID userId) {
        User user = userService.getById(userId);
        List<Review> reviews = reviewService.getReviewsForUser(userId);
        
        ModelAndView mav = new ModelAndView("review/user-reviews");
        mav.addObject("user", user);
        mav.addObject("reviews", reviews);
        return mav;
    }

    @GetMapping("/user/{userId}/new")
    public ModelAndView showNewReviewForm(@PathVariable UUID userId) {
        User user = userService.getById(userId);
        ModelAndView mav = new ModelAndView("review/new-review");
        mav.addObject("user", user);
        mav.addObject("createReviewDto", new CreateReviewDto());
        return mav;
    }

    @PostMapping("/user/{userId}/new")
    public ModelAndView createReview(@PathVariable UUID userId, 
                                   @AuthenticationPrincipal AuthenticationDetails details, 
                                   @Valid @ModelAttribute("createReviewDto") CreateReviewDto reviewDto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("review/new-review");
            mav.addObject("user", userService.getById(userId));
            mav.addObject("createReviewDto", reviewDto);
            return mav;
        }
        
        ModelAndView mav = new ModelAndView();
        reviewService.createReview(userId, details.getUserId(), reviewDto.getStars(), reviewDto.getMessage());
        mav.setViewName("redirect:/api/reviews/" + userId);
        return mav;
    }

    @GetMapping("/api/my-reviews")
    public ResponseEntity<List<Review>> getMyReviewsApi(@AuthenticationPrincipal AuthenticationDetails details) {
        User user = userService.getById(details.getUserId());
        List<Review> reviews = reviewService.getReviewsForUser(user.getId());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/api/{userId}")
    public ResponseEntity<List<Review>> getUserReviewsApi(@PathVariable UUID userId) {
        User user = userService.getById(userId);
        List<Review> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/api/user/{userId}/new")
    public ResponseEntity<Void> createReviewApi(@PathVariable UUID userId,
                                              @AuthenticationPrincipal AuthenticationDetails details,
                                              @Valid @RequestBody CreateReviewDto reviewDto) {
        reviewService.createReview(userId, details.getUserId(), reviewDto.getStars(), reviewDto.getMessage());
        return ResponseEntity.created(java.net.URI.create("/api/reviews/" + userId)).build();
    }
} 