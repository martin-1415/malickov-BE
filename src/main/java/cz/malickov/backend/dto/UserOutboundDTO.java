package cz.malickov.backend.dto;

import cz.malickov.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutboundDTO {


    private String lastName;

    private String firstName;

    private String email;

    private boolean active;

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
