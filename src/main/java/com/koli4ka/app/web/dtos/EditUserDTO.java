package com.koli4ka.app.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditUserDTO {
    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 20, message = "Името трябва да е между 2 и 20 символа")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна")
    @Size(min = 2, max = 20, message = "Фамилията трябва да е между 2 и 20 символа")
    private String lastName;

    @NotBlank(message = "Профилната снимка е задължителна")
    private String imageUrl;
} 