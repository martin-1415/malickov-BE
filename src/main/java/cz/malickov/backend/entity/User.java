package cz.malickov.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

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
    private Integer userId;

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
    private String email;

    @JsonProperty("createdAt")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty("passwordHash")
    @Column(name = "password_hash")
    @ToString.Exclude
    private String passwordHash;

    @JsonProperty("active")
    @Column(name = "active")
    @NotNull(message = "Active must be set.")
    private boolean active;

    @JsonProperty("userRole")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role userRole;

    public User(String lastName, String firstName, String email, boolean active, Role user_role){
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.active = active;
        this.userRole = user_role;
    }

    public User() {

    }
}
