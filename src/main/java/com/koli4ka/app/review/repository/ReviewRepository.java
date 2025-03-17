package com.koli4ka.app.review.repository;


import com.koli4ka.app.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository   extends JpaRepository<Review, UUID> {
}
