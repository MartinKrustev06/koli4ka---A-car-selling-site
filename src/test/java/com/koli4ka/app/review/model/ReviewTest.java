package com.koli4ka.app.review.model;

import com.koli4ka.app.user.model.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    private Validator validator;
    private User reviewedUser;
    private User author;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        reviewedUser = new User();
        reviewedUser.setId(UUID.randomUUID());
        reviewedUser.setUsername("reviewedUser");

        author = new User();
        author.setId(UUID.randomUUID());
        author.setUsername("author");
    }

    @Test
    void testValidReview() {
        Review review = Review.builder()
                .reviewedUser(reviewedUser)
                .author(author)
                .stars(5)
                .message("Great service!")
                .build();

        var violations = validator.validate(review);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidStars() {
        Review review = Review.builder()
                .reviewedUser(reviewedUser)
                .author(author)
                .stars(6) // Invalid: should be between 1 and 5
                .message("Great service!")
                .build();

        var violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testEmptyMessage() {
        Review review = Review.builder()
                .reviewedUser(reviewedUser)
                .author(author)
                .stars(5)
                .message("") // Invalid: should not be empty
                .build();

        var violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testNullReviewedUser() {
        Review review = Review.builder()
                .author(author)
                .stars(5)
                .message("Great service!")
                .build();

        var violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testNullAuthor() {
        Review review = Review.builder()
                .reviewedUser(reviewedUser)
                .stars(5)
                .message("Great service!")
                .build();

        var violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testBuilderPattern() {
        UUID expectedId = UUID.randomUUID();
        Review review = Review.builder()
                .id(expectedId)
                .reviewedUser(reviewedUser)
                .author(author)
                .stars(5)
                .message("Great service!")
                .build();

        assertNotNull(review.getId());
        assertEquals(expectedId, review.getId());
        assertEquals(reviewedUser, review.getReviewedUser());
        assertEquals(author, review.getAuthor());
        assertEquals(5, review.getStars());
        assertEquals("Great service!", review.getMessage());
    }
} 