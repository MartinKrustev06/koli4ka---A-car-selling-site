package com.koli4ka.app.web.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewDto {
    @Min(1)
    @Max(5)
    private int stars;

    @NotBlank(message = "Message cannot be empty")
    private String message;
} 