package cz.malickov.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInboundDTO {
    private Integer userId;
    private String lastName;
    private String firstName;
    private String email;
}