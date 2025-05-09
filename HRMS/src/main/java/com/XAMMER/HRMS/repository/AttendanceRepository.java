package com.XAMMER.HRMS.repository;

import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUserAndCheckInTimeBetween(User user, LocalDateTime startTime, LocalDateTime endTime);

    Optional<Attendance> findTopByUserOrderByCheckInTimeDesc(User user);

    List<Attendance> findByCheckOutTimeIsNullAndCheckInTimeBefore(LocalDateTime dateTime);

    List<Attendance> findByCheckInTimeBetweenOrderByUser_UsernameAscCheckInTimeAsc(LocalDateTime startTime, LocalDateTime endTime);

    void deleteByUser(User user);

    List<Attendance> findByUserAndCheckInTimeBetweenOrderByCheckInTimeDesc(User user, LocalDateTime startTime, LocalDateTime endTime);

    Optional<Attendance> findTopByUserAndCheckInTimeBetweenOrderByCheckInTimeDesc(User user, LocalDateTime startTime, LocalDateTime endTime);

    // Add this missing method:
    List<Attendance> findByUserAndCheckInTimeBetweenOrderByCheckInTimeAsc(User user, LocalDateTime startTime, LocalDateTime endTime);

    List<Attendance> findByUserAndAttendanceDate(User user, LocalDate today);

    List<Attendance> findByUserAndAttendanceDateOrderByCheckInTimeDesc(User user, LocalDate today);

    Optional<Attendance> findByUserIdAndAttendanceDateAndCheckOutTimeIsNull(Long id, LocalDate yesterday);

    List<Attendance> findByAttendanceDateAndCheckOutTimeIsNull(LocalDate yesterday);

    Optional<User> findByUserIdAndAttendanceDate(Long id, LocalDate today);
}