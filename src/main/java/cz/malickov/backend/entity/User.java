package cz.malickov.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.malickov.backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter @Setter @ToString
@AllArgsConstructor
@Builder
public class User {

    @Id
    @JsonProperty("userId")
    @Column(name = "user_id",nullable = false,columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @JsonProperty("lastName")
    @Column(name = "last_name")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @JsonProperty("firstName")
    @Column(name = "first_name")
    @NotBlank(message = "First name is required")
    private String firstName;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @JsonProperty("createdAt")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty("password")
    @Column(name = "password")
    @ToString.Exclude
    private String password;

    @JsonProperty("active")
    @Column(name = "active")
    @NotNull(message = "Active must be set.")
    private boolean active;

    @JsonProperty("roleName")
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role roleName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    public User(String lastName, String firstName, String email, boolean active, Role roleName){
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.active = active;
        this.roleName = roleName;
    }

    public User() {

    }
}
