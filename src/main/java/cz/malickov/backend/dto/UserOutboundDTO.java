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

    private Long userId;

    private String lastName;

    private String firstName;

    private String email;

    private boolean active;

    private String roleName ;


    public static UserOutboundDTO userOutboundDTOfromEntity(User user) {
        return new UserOutboundDTO(
                user.getUserId(),
                user.getLastName(),
                user.getFirstName(),
                user.getEmail(),
                user.isActive(),
                user.getRoleName().name()
                );
    }
}
