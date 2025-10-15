package cz.malickov.backend.dto;
import cz.malickov.backend.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInboundDTO {

    private Long id; // null for creation
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email(message = "Email is not valid")
    private String email;
    @NotNull
    private boolean active;
    @NotNull
    private Role roleName;

    public UserInboundDTO(String firstName, String lastName, String email, Boolean active, Role roleName){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.roleName = roleName;
    }
}