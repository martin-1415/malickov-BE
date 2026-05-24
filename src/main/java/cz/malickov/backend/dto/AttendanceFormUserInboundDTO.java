package cz.malickov.backend.dto;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;


public record AttendanceFormUserInboundDTO(
        List<monthPlan> monthPlans
) {

    public record monthPlan(
            UUID childUUID,
            YearMonth month,
            List<DayPlan> dayPlans
    ) {
    }

    public record DayPlan(
            UUID attendanceUUID,
            @NotNull
            LocalDate day,  // only day is taken, not year or month
            @NotNull
            LocalDateTime planArrival, // only time is taken, not day, month or year
            @NotNull
            LocalDateTime planLeaving, // only time is taken, not day, month or year
            String excuse
    ) {
    }
}