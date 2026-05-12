package cz.malickov.backend.controller;

import cz.malickov.backend.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    private final AttendanceService attendanceService;

//    @PostMapping("/monthly")
//    public ResponseEntity<Void> saveMonthlyAttendance(
//            @RequestBody List<FullAttendanceDto> attendanceList
//    ) {
//
//        attendanceService.saveMonthlyAttendance(attendanceList, 1);
//
//        return ResponseEntity.ok().build();
//    }
//
//    @Transactional
//    public void saveAttendance(AttendanceDto dto) {
//
//        Authentication auth =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        boolean isParent = auth.getAuthorities()
//                .stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
//
//        if (isParent) {
//
//            if (!canParentEdit(dto.date())) {
//                throw new AccessDeniedException(
//                        "Parents can edit only next month attendance");
//            }
//        }
//
//        // save logic here
//    }

    private boolean canParentEdit(LocalDate attendanceDate) {

        LocalDate now = LocalDate.now();

        YearMonth nextMonth = YearMonth.from(now).plusMonths(1);

        return YearMonth.from(attendanceDate)
                .equals(nextMonth);
    }

}
