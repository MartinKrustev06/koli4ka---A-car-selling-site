package com.koli4ka.app.web;

import com.koli4ka.app.review.model.Review;
import com.koli4ka.app.review.service.ReviewService;
import com.koli4ka.app.security.AuthenticationDetails;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.model.UserRole;
import com.koli4ka.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ReviewController.class)
class ReviewControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    private UUID userId;
    private UUID reviewerId;
    private List<Review> reviews;
    private User testUser;
    private User reviewerUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        reviewerId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);
        
        reviewerUser = new User();
        reviewerUser.setId(reviewerId);
        reviewerUser.setUsername("reviewer");
        reviewerUser.setRole(UserRole.USER);
        
        reviews = Arrays.asList(
            createReview(reviewerUser, testUser, 5, "Great service!"),
            createReview(reviewerUser, testUser, 4, "Good experience")
        );

        when(reviewService.getReviewsForUser(any())).thenReturn(reviews);
        when(userService.getById(any())).thenReturn(testUser);
    }

    @Test
    void getUserReviews_ShouldReturnUserReviewsView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/{userId}", userId)
                .with(SecurityMockMvcRequestPostProcessors.user(new AuthenticationDetails(reviewerId, "reviewer", "password", UserRole.USER))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("review/user-reviews"))
                .andExpect(MockMvcResultMatchers.model().attribute("reviews", reviews))
                .andExpect(MockMvcResultMatchers.model().attribute("user", testUser));
    }

    @Test
    void createReview_ShouldRedirectToUserReviews() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews/user/{userId}/new", userId)
                .with(SecurityMockMvcRequestPostProcessors.user(new AuthenticationDetails(reviewerId, "reviewer", "password", UserRole.USER)))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("stars", "5")
                .param("message", "Great service!"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/api/reviews/" + userId));
    }

    private Review createReview(User author, User reviewedUser, int stars, String message) {
        return Review.builder()
                .author(author)
                .reviewedUser(reviewedUser)
                .stars(stars)
                .message(message)
                .build();
    }
} 