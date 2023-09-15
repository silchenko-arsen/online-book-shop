package com.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 50)
    private String password;
}
