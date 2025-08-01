package cz.malickov.backend.dto;

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
}
