package cz.malickov.backend.controller;

import cz.malickov.backend.dto.AttendancePlanDTO;
import cz.malickov.backend.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }


    @PostMapping("/savePlan")
    public ResponseEntity<Void> saveMonthlyAttendance(
            @RequestBody @Valid AttendancePlanDTO attendancePlanForm,
            @CookieValue(value = "JWT") String token
    ) {

        attendanceService.savePlan(attendancePlanForm, token);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

}
