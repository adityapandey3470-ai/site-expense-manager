package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByIdAndDeletedFalse(Long id);

    List<Attendance> findAllByDeletedFalseAndSiteDeletedFalse();

    List<Attendance> findAllBySiteIdAndDeletedFalseAndSiteDeletedFalse(Long siteId);

    boolean existsBySiteIdAndAttendanceDateAndDeletedFalse(Long siteId, LocalDate attendanceDate);

    boolean existsBySiteIdAndAttendanceDateAndDeletedFalseAndIdNot(Long siteId, LocalDate attendanceDate, Long id);

    boolean existsBySite_Id(Long siteId);
}
