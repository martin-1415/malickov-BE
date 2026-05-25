package cz.malickov.backend.service;

import cz.malickov.backend.dto.AttendanceFormUserInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.repository.AttendanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class AttendanceService {
    private final ChildService childService;
    private final JWTService jwtService;
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(
                             ChildService childService,
                             JWTService jwtService,
                             AttendanceRepository attendanceRepository) {
        this.childService = childService;
        this.jwtService = jwtService;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public void savePlan(AttendanceFormUserInboundDTO attendancePlanForm, String token) {
        UUID userUuid = jwtService.extractUserUuid(token);
        for (var plan : attendancePlanForm.monthPlans()) {
            UUID childUuid = plan.childUUID();
            YearMonth month = plan.month();

            if(!this.canParentEditPlan(childUuid, userUuid, month)){
                throw new AccessDeniedException("You are not authorized to edit this record.");
            }


            this.attendanceRepository.save()

        }

    }

   /*
    * Parent can edit plan for next monts and only it's children
    */
    private boolean canParentEditPlan(UUID childUuid, UUID userUuid, YearMonth attendanceMonth) {

        boolean authorizedParent = this.childService.getActiveChildrenByUserUuid(userUuid)
                .stream()
                .map(ChildOutboundDTO::childUuid)
                .toList().contains(childUuid);

        int tolerance = 7; // 7-hour tolerance to fill in plan before the month ends
        YearMonth thisMonth =
                YearMonth.from(LocalDateTime.now().minusHours(tolerance));
        boolean futureMonth = thisMonth.isBefore(attendanceMonth);

        return authorizedParent && futureMonth;
    }
}
