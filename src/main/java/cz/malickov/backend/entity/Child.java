package cz.malickov.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.malickov.backend.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "child")
@Getter @Setter @ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Child {

    @Id
    @JsonProperty("childId")
    @Column(name = "child_id",nullable = false,columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int childId;

    @Column(name = "first_name")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Last name is required")
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

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Attendance> attendanceList = new ArrayList<>();

}
