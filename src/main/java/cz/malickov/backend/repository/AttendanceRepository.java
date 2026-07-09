package cz.malickov.backend.repository;


import cz.malickov.backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByChild_ChildUuidAndAttendanceDate(
            UUID childUuid,
            LocalDate attendanceDate
    );
}
