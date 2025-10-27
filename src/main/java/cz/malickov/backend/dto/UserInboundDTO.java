package cz.malickov.backend.dto;
import cz.malickov.backend.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserInboundDTO (
    Long id, // can be null for creation
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    @Email(message = "Email is not valid")
    String email,
    @NotNull
    boolean active,
    @NotNull
    Role roleName){}