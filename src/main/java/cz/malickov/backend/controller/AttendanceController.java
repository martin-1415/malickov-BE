package cz.malickov.backend.controller;

import cz.malickov.backend.dto.AttendancePlanParentInboundDTO;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }


    @PostMapping("/parentSavePlan")
    public ResponseEntity<String> parentSaveAttendancePlan(
            @RequestBody @Valid AttendancePlanParentInboundDTO attendancePlanForm,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        attendanceService.savePlan(attendancePlanForm, userDetails);
        return ResponseEntity.ok().body("Plan saved");
    }

}
