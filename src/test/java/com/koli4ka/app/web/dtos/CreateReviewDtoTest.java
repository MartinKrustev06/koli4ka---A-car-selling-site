package com.koli4ka.app.web.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateReviewDtoTest {
    private Validator validator;
    private CreateReviewDto dto;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        dto = new CreateReviewDto();
    }

    @Test
    void testValidDto() {
        dto.setStars(5);
        dto.setMessage("Great service!");

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidStars() {
        dto.setStars(6); // Invalid: should be between 1 and 5
        dto.setMessage("Great service!");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testEmptyMessage() {
        dto.setStars(5);
        dto.setMessage(""); // Invalid: should not be empty

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testNullMessage() {
        dto.setStars(5);
        dto.setMessage(null); // Invalid: should not be null

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testGetterAndSetter() {
        dto.setStars(5);
        dto.setMessage("Great service!");

        assertEquals(5, dto.getStars());
        assertEquals("Great service!", dto.getMessage());
    }
} 