package cz.malickov.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.malickov.backend.enums.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "attendance")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {

    @Id
    @JsonProperty("attendanceId")
    @Column(name = "attendance_id",nullable = false,columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attendanceId;

    @JsonProperty("childId")
    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child childId;

    @Column(name = "date", nullable = false)
    private Date date;

    @JsonProperty("planArrival")
    @Column(name = "plan_arrival", nullable = false)
    private LocalTime planArrival;

    @JsonProperty("planLeaving")
    @Column(name = "plan_leaving", nullable = false)
    private LocalTime planLeaving;

    @JsonProperty("arrival")
    @Column(name = "arrival", nullable = true)
    private LocalTime arrival;

    @JsonProperty("leaving")
    @Column(name = "leaving", nullable = true)
    private LocalTime leaving;

    @JsonProperty("excuse")
    @Column(name = "excuse", nullable = true)
    private String excuse;

    @JsonProperty("lateExcuse")
    @Column(name = "late_excuse", nullable = true)
    private String lateExcuse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "modified_by")
    private int modifiedBy;

    @JsonProperty("department")
    @Column(name = "department", nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;
}
