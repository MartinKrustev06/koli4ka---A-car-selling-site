package com.koli4ka.app.web;

import com.koli4ka.app.review.model.Review;
import com.koli4ka.app.review.service.ReviewService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import com.koli4ka.app.web.dtos.CreateReviewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    private User testUser;
    private User testSeller;
    private AuthenticationDetails authDetails;
    private UUID userId;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sellerId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testSeller = new User();
        testSeller.setId(sellerId);
        testSeller.setUsername("testseller");
        testSeller.setRole(UserRole.USER);
        testSeller.setFirstName("Test");
        testSeller.setLastName("Seller");

        authDetails = new AuthenticationDetails(userId, "testuser", "password", UserRole.USER);
    }

    @Test
    void getMyReviews_ShouldReturnMyReviewsView() throws Exception {
        // Arrange
        when(userService.getById(userId)).thenReturn(testUser);
        when(reviewService.getReviewsForUser(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/reviews/my-reviews")
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("review/my-reviews"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getUserReviews_ShouldReturnUserReviewsView() throws Exception {
        // Arrange
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getById(sellerId)).thenReturn(testSeller);
        when(reviewService.getReviewsForUser(sellerId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/reviews/{userId}", sellerId)
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("review/user-reviews"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("seller"));
    }

    @Test
    void showNewReviewForm_ShouldReturnNewReviewView() throws Exception {
        // Arrange
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getById(sellerId)).thenReturn(testSeller);

        // Act & Assert
        mockMvc.perform(get("/reviews/user/{userId}/new", sellerId)
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("review/new-review"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("seller"))
                .andExpect(model().attributeExists("createReviewDto"));
    }

    @Test
    void createReview_ShouldRedirectToUserReviews() throws Exception {
        // Arrange
        CreateReviewDto reviewDto = new CreateReviewDto();
        reviewDto.setStars(5);
        reviewDto.setMessage("Great service!");
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getById(sellerId)).thenReturn(testSeller);

        // Act & Assert
        mockMvc.perform(post("/reviews/user/{sellerId}/new", sellerId)
                .param("stars", String.valueOf(reviewDto.getStars()))
                .param("message", reviewDto.getMessage())
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/reviews/" + sellerId));
    }

    @Test
    void createReview_WithInvalidData_ShouldThrow() throws Exception {
        // Arrange
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.getById(sellerId)).thenReturn(testSeller);

        // Act & Assert
        mockMvc.perform(post("/reviews/user/{userId}/new", sellerId)
                .with(SecurityMockMvcRequestPostProcessors.user(authDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("stars", "6")
                .param("message", ""))
                .andExpect(status().is4xxClientError());
    }

    private Review createReview(User reviewedUser, User author, int stars, String message) {
        return Review.builder()
                .reviewedUser(reviewedUser)
                .author(author)
                .stars(stars)
                .message(message)
                .build();
    }
} 