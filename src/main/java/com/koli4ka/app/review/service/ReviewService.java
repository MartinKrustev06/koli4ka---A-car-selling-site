package com.koli4ka.app.review.service;

import com.koli4ka.app.review.model.Review;
import com.koli4ka.app.review.repository.ReviewRepository;
import com.koli4ka.app.user.model.User;
import com.koli4ka.app.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public List<Review> getReviewsForUser(UUID userId) {
        User user = userService.getById(userId);
        return reviewRepository.findByReviewedUser(user);
    }


    public void createReview(UUID sellerId, UUID reviewerId, int stars, String message) {
        User seller = userService.getById(sellerId);
        User reviewer = userService.getById(reviewerId);


        Review review = new Review();
        review.setReviewedUser(seller);
        review.setAuthor(reviewer);
        review.setStars(stars);
        review.setMessage(message);

        reviewRepository.save(review);
    }





} 