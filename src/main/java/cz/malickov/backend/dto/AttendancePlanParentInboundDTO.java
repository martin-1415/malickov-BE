package cz.malickov.backend.dto;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record AttendancePlanParentInboundDTO(
        List<UUID> children,
        List<DayPlan> days
) {
    public record DayPlan(
            @NotNull
            LocalDate day,  // full date including Year, month and day
            @NotNull
            LocalDateTime planArrival, // only time is taken, not day, month or year
            @NotNull
            LocalDateTime planLeaving // only time is taken, not day, month or year
    ) {
    }
}