package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.repository.AttendanceRepository;
import com.XAMMER.HRMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    // Refactored method to get User or throw exception
    private User getUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    IllegalArgumentException e = new IllegalArgumentException("User not found: " + username);
                    logger.error("User not found: {}", username, e); // Log the error
                    return e;
                });
    }

    // Refactored checkIn logic
    @Override
    public Attendance checkIn(String username) {
        User user = getUserOrThrowException(username);
        LocalDate today = LocalDate.now();

        if (!canCheckIn(user)) {
            String errorMessage = "User " + username + " is already checked in today.";
            logger.warn(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceDate(today);
        attendance.setUsername(username); //redundant, user object has the username.
        Attendance savedAttendance = attendanceRepository.save(attendance);
        logger.info("User {} checked in at {}", username, savedAttendance.getCheckInTime());
        return savedAttendance;
    }

    //Refactored checkOut logic
    @Override
    public Attendance checkOut(String username) {
        User user = getUserOrThrowException(username);
        Attendance latestCheckIn = attendanceRepository.findTopByUserOrderByCheckInTimeDesc(user)
                .filter(a -> a.getCheckOutTime() == null)
                .orElseThrow(() -> {
                    String errorMessage = "No active check-in found for user: " + username;
                    logger.warn(errorMessage);
                    return new IllegalStateException(errorMessage);
                });

        latestCheckIn.setCheckOutTime(LocalDateTime.now());
        Attendance updatedAttendance = attendanceRepository.save(latestCheckIn);
        logger.info("User {} checked out at {}", username, updatedAttendance.getCheckOutTime());
        return updatedAttendance;
    }

    @Override
    public void recordCheckIn(String username) {
        User user = getUserOrThrowException(username);
        LocalDate today = LocalDate.now();

        if (!canCheckIn(user)) {
            String errorMessage = "User " + username + " is already checked in today.";
            logger.warn(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceDate(today);
        attendance.setUsername(username); //redundant
        attendanceRepository.save(attendance);
        logger.info("User {} check-in recorded at {}", username, attendance.getCheckInTime());
    }

    @Override
    public void recordCheckOut(String username) {
        User user = getUserOrThrowException(username);
        Attendance latestCheckIn = attendanceRepository.findTopByUserOrderByCheckInTimeDesc(user)
                .filter(a -> a.getCheckOutTime() == null)
                .orElse(null);

        if (latestCheckIn != null) {
            latestCheckIn.setCheckOutTime(LocalDateTime.now());
            attendanceRepository.save(latestCheckIn);
            logger.info("User {} check-out recorded at {}", username, latestCheckIn.getCheckOutTime());
        }
    }

    @Override
    public void timeoutIncompleteSessions() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusHours(12);
        List<Attendance> incompleteSessions = attendanceRepository.findByCheckOutTimeIsNullAndCheckInTimeBefore(timeoutTime);
        incompleteSessions.forEach(session -> {
            session.setCheckOutTime(timeoutTime);
            logger.warn("User {} session timed out at {}", session.getUser().getUsername(), timeoutTime); // Log each timeout
        });
        attendanceRepository.saveAll(incompleteSessions);
    }

    @Override
    public List<Attendance> getDailyAttendance(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Attendance> attendances = attendanceRepository.findByCheckInTimeBetweenOrderByUser_UsernameAscCheckInTimeAsc(startOfDay, endOfDay);
        logger.info("Retrieved daily attendance for date: {}", date);
        return attendances;
    }

    @Override
    @Transactional
    public void resetAttendance(String username) {
        User user = getUserOrThrowException(username);
        attendanceRepository.deleteByUser(user);
        logger.warn("Attendance reset for user: {}", username);
    }

    @Override
    public List<Attendance> getAttendanceByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserOrThrowException(username);
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        List<Attendance> attendances = attendanceRepository.findByUserAndCheckInTimeBetweenOrderByCheckInTimeAsc(user, startOfDay, endOfDay);
        logger.info("Retrieved attendance for user {} between {} and {}", username, startDate, endDate);
        return attendances;
    }

    @Override
    public Attendance getTodayAttendance(String username) {
        User user = getUserOrThrowException(username);
        LocalDate today = LocalDate.now();
        Optional<Attendance> attendance = attendanceRepository.findTopByUserAndCheckInTimeBetweenOrderByCheckInTimeDesc(
                user, today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );
        Attendance result = attendance.orElse(null); // changed from orElse(null)
        if(result != null){
            logger.info("Retrieved today's attendance for user: {}", username);
        }
        return result;
    }

    @Override
    public void resetCheckoutTime(String username, LocalDate date) {
        User user = getUserOrThrowException(username);
        List<Attendance> attendanceList = attendanceRepository.findByUserAndCheckInTimeBetween(
                user, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        attendanceList.forEach(attendance -> {
            attendance.setCheckOutTime(null);
            logger.warn("Checkout time reset for user {} on date {}", username, date);
        });
        attendanceRepository.saveAll(attendanceList);
    }

    @Override
    @Transactional
    public void deleteAllAttendanceRecords(String username) {
        User user = getUserOrThrowException(username);
        attendanceRepository.deleteByUser(user);
        logger.warn("All attendance records deleted for user: {}", username);
    }

    @Override
    public void resetForTodayCheckIn(String username) {
        User user = getUserOrThrowException(username);
        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findByUserAndCheckInTimeBetween(
                user, today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        todayAttendance.forEach(attendance -> {
            attendance.setCheckInTime(null);
            logger.warn("Check-in time reset for user {} today", username);
        });
        attendanceRepository.saveAll(todayAttendance);
    }

    //canCheckIn now uses User object.
    public boolean canCheckIn(User user) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByUserAndCheckInTimeBetween(
                user, today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        ).stream().noneMatch(a -> a.getCheckOutTime() == null); // changed to noneMatch
    }

    @Override
    public Object getDailyAttendance() {
        throw new UnsupportedOperationException("Unimplemented method 'getDailyAttendance'");
    }

    @Override
    public boolean canCheckIn(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canCheckIn'");
    }
}
