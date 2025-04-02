package com.koli4ka.app.user.model;

import com.koli4ka.app.car.model.Car;
import com.koli4ka.app.favoriteCar.model.FavoriteCar;
import com.koli4ka.app.review.model.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true, nullable = false)
    private String phone;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToMany(mappedBy = "publisher")
    private List<Car> cars;
    @OneToMany(mappedBy = "reviewedUser")
    private List<Review> receivedReviews;
    @OneToMany(mappedBy = "author")
    private List<Review> publishedReviews;
    @OneToMany(mappedBy = "favoriteBy")
    private List<FavoriteCar> favoriteCars;
}
