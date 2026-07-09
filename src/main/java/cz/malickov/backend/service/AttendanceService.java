package cz.malickov.backend.service;

import cz.malickov.backend.dto.AttendancePlanParentInboundDTO;
import cz.malickov.backend.dto.ChildOutboundDTO;
import cz.malickov.backend.entity.Attendance;
import cz.malickov.backend.entity.Child;
import cz.malickov.backend.exception.childExceptions.ChildNotFoundException;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.repository.AttendanceRepository;
import cz.malickov.backend.repository.ChildRepository;
import cz.malickov.backend.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class AttendanceService {
    private final ChildService childService;
    private final AttendanceRepository attendanceRepository;
    private final ChildRepository childRepository;
    private final HolidayRepository holidayRepository;

    public AttendanceService(
                             ChildService childService,
                             AttendanceRepository attendanceRepository,
                             ChildRepository childRepository,
                             HolidayRepository holidayRepository) {
        this.childService = childService;
        this.attendanceRepository = attendanceRepository;
        this.childRepository = childRepository;
        this.holidayRepository = holidayRepository;
    }

    @Transactional
    public void savePlan(AttendancePlanParentInboundDTO attendancePlanForm,
                         CustomUserDetails userDetails) {

        UUID userUuid = userDetails.getUser().getUserUuid();

        for (UUID childUuid : attendancePlanForm.children()) {

            Child child = childRepository.findById(childUuid)
                    .orElseThrow(() -> new ChildNotFoundException(childUuid));

            if (!canParentEditChild(childUuid, userUuid)) {
                continue;
            }

            for (AttendancePlanParentInboundDTO.DayPlan oneDay : attendancePlanForm.days()) {
                if(!canParentEditDay(oneDay.day())){
                    continue;
                }
                Attendance attendance = attendanceRepository
                        .findByChild_ChildUuidAndAttendanceDate(
                                childUuid,
                                oneDay.day())
                        .orElseGet(Attendance::new);

                attendance.setChild(child);
                attendance.setAttendanceDate(oneDay.day());

                attendance.setPlanArrival(
                        oneDay.planArrival().toLocalTime()
                );

                attendance.setPlanLeaving(
                        oneDay.planLeaving().toLocalTime()
                );

                attendance.setDepartment(child.getDepartment());
                attendance.setModifiedBy(userDetails.getUser().getUserUuid());
                attendance.setModifiedAt(LocalDateTime.now());

                attendanceRepository.save(attendance);
            }
        }
    }

    /*
     * Parent can only it's children
     */
    private boolean canParentEditChild(UUID childUuid, UUID userUuid) {

        boolean authorizedParent = this.childService.getActiveChildrenByUserUuid(userUuid)
                .stream()
                .map(ChildOutboundDTO::childUuid)
                .toList().contains(childUuid);

        return authorizedParent;
    }

   /*
    * Parent can edit plan for next months
    */
   private boolean canParentEditDay(LocalDate attendanceDay) {
       int tolerance = 7; // 7-hour tolerance

       YearMonth currentMonth =
               YearMonth.from(LocalDateTime.now().minusHours(tolerance));

       boolean futureMonth =
               currentMonth.isBefore(YearMonth.from(attendanceDay));

       boolean holiday =
               holidayRepository.existsByHolidayDate(attendanceDay);

       return futureMonth && !holiday;
   }
}
