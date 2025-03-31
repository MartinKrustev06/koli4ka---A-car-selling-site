package com.koli4ka.app.web.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class EditUserRequest {


    @NotNull(message="Трябва да попълните полето")
    String firstName;
    @NotNull(message="Трябва да попълните полето")
    String lastName;
    @NotNull(message="Трябва да попълните полето")
    String phoneNumber;
    @NotNull(message="Трябва да попълните полето")
    @URL(message = "Трябва да попълните с валидел линк")
    String imageUrl;
}
