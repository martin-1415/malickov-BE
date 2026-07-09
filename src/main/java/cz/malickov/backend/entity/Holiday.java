package cz.malickov.backend.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDate;

@Entity
@Table(
        name = "holiday",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_date",
                        columnNames = "holiday_date"
                )
        }
)
@Getter
public class Holiday {

    @Id
    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;
}