package com.koli4ka.app.web.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


@Data
public class RegisterRequest {


    @NotNull(message = "Username should not be null!")
    @Size(min = 5, max = 25, message = "Username should be between 5 and 25 characters")
    private String username;

    @NotNull(message = "Password should not be null!")
    @Size(min = 3, message = "Password should be at least 3 characters!")
    private String password;

    @Email(message = "Invalid email format!")
    @NotNull(message = "Email should not be null")
    private String email;

    @NotNull(message = "Phone number should not be null")
    private String phoneNumber;

    @NotNull(message = "First name should not be null!")
    private String firstName;

    @NotNull(message = "Last name should not be null!")
    private String lastName;

    @URL(message = "Should be a valid url")
    private String imageUrl;



}
