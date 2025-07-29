package cz.malickov.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role")
@NoArgsConstructor
@Getter @Setter @ToString
public class Role {
    @Id
    @JsonProperty("roleId")
    @Column(name = "role_id",nullable = false,columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    @JsonProperty("roleName")
    @Column(name = "role_name",nullable = false)
    private String roleName;

    public Role(String roleName){
        this.roleName = roleName;
    }
}
