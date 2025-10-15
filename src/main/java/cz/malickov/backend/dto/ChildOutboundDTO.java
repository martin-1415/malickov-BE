package cz.malickov.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.enums.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildOutboundDTO {


    private Long id;

    private String firstName;

    private String lastName;

    @JsonProperty("department")
    @Column(name = "department", nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;

    @Column(name = "birthday")
    private Date birthDay;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "identificator_id")
    private Identificator identificator;

    @Column(name = "mon")
    private Boolean mon;

    @Column(name = "tue")
    private Boolean tue;

    @Column(name = "wed")
    private Boolean wed;

    @Column(name = "thu")
    private Boolean thu;

    @Column(name = "fri")
    private Boolean fri;
}
