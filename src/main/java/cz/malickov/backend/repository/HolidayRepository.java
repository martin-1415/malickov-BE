package cz.malickov.backend.repository;

import cz.malickov.backend.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, LocalDate> {
    boolean existsByHolidayDate(LocalDate holidayDate);
}