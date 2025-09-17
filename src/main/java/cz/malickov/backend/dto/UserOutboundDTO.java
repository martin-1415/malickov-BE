package cz.malickov.backend.dto;

import cz.malickov.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutboundDTO {

    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    @Email
    @NotBlank
    private String email;
    @NotNull
    private boolean active;
    @NotNull
    private String roleName ;


    public static UserOutboundDTO UserOutboundDTOfromEntity(User user) {
        return new UserOutboundDTO(
                user.getLastName(),
                user.getFirstName(),
                user.getEmail(),
                user.isActive(),
                user.getRoleName().name()
                );
    }
}
