package com.koli4ka.app.review.model;

import com.koli4ka.app.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @NotNull
    private User reviewedUser;

    @ManyToOne
    @NotNull
    private User author;

    @Column( nullable = false)
    @Min(1)
    @Max(5)
    private int stars;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Message cannot be empty")
    private String message;
} 