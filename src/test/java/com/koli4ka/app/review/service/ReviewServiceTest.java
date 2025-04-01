package com.koli4ka.app.review.service;

import com.koli4ka.app.review.model.Review;
import com.koli4ka.app.review.repository.ReviewRepository;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private User seller;
    private User reviewer;
    private UUID sellerId;
    private UUID reviewerId;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        reviewerId = UUID.randomUUID();

        seller = new User();
        seller.setId(sellerId);
        seller.setUsername("seller");

        reviewer = new User();
        reviewer.setId(reviewerId);
        reviewer.setUsername("reviewer");
    }

    @Test
    void testGetReviewsForUser() {
        // Arrange
        List<Review> expectedReviews = Arrays.asList(
            createReview(seller, reviewer, 5, "Great!"),
            createReview(seller, reviewer, 4, "Good!")
        );
        when(userService.getById(sellerId)).thenReturn(seller);
        when(reviewRepository.findByReviewedUser(seller)).thenReturn(expectedReviews);

        // Act
        List<Review> actualReviews = reviewService.getReviewsForUser(sellerId);

        // Assert
        assertEquals(expectedReviews, actualReviews);
        verify(userService).getById(sellerId);
        verify(reviewRepository).findByReviewedUser(seller);
    }

    @Test
    void testGetReviewsForUser_UserNotFound() {
        // Arrange
        when(userService.getById(sellerId)).thenThrow(new EntityNotFoundException("User not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> reviewService.getReviewsForUser(sellerId));
        verify(userService).getById(sellerId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void testCreateReview() {
        // Arrange
        when(userService.getById(sellerId)).thenReturn(seller);
        when(userService.getById(reviewerId)).thenReturn(reviewer);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        reviewService.createReview(sellerId, reviewerId, 5, "Great service!");

        // Assert
        verify(userService).getById(sellerId);
        verify(userService).getById(reviewerId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testCreateReview_SellerNotFound() {
        // Arrange
        when(userService.getById(sellerId)).thenThrow(new EntityNotFoundException("Seller not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            reviewService.createReview(sellerId, reviewerId, 5, "Great service!"));
        verify(userService).getById(sellerId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void testCreateReview_ReviewerNotFound() {
        // Arrange
        when(userService.getById(sellerId)).thenReturn(seller);
        when(userService.getById(reviewerId)).thenThrow(new EntityNotFoundException("Reviewer not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            reviewService.createReview(sellerId, reviewerId, 5, "Great service!"));
        verify(userService).getById(sellerId);
        verify(userService).getById(reviewerId);
        verifyNoInteractions(reviewRepository);
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