package cz.malickov.backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record UserLoginDTO (
    @NotBlank
    @Email(message = "Email is not valid")
    String email,
    @NotBlank
    String password
){}
