package com.koli4ka.app.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "Username must not be null!")
    @Size(min = 5, max = 25, message = "Username must be between 5 and 25 characters!")
    private String username;

    @NotNull(message = "Password must not be null!")
    @Size(min = 3, message = "Password must be at least 3 characters long!")
    private String password;
}
