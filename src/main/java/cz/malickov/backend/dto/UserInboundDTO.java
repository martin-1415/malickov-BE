package cz.malickov.backend.dto;
import cz.malickov.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInboundDTO {
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private Role roleName;
}