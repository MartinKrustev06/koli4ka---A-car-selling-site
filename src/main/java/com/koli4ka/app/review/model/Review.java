package com.koli4ka.app.review.model;

import com.koli4ka.app.user.model.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(optional = false)
    private User author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int stars;




}
