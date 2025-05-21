package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.repository.AttendanceRepository;
import com.XAMMER.HRMS.repository.UserRepository; // Assuming you have a UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface AttendanceService {
    Attendance checkIn(String username);
    Attendance checkOut(String username);
    void recordCheckIn(String username);
    void recordCheckOut(String username);
    void timeoutIncompleteSessions();
    List<Attendance> getDailyAttendance(LocalDate date);
    void resetAttendance(String username);
    List<Attendance> getAttendanceByDateRange(String username, LocalDate startDate, LocalDate endDate);
    Attendance getTodayAttendance(String username);
    void resetCheckoutTime(String username, LocalDate date);
    void deleteAllAttendanceRecords(String username);
    void resetForTodayCheckIn(String username);
    boolean canCheckIn(String username);
    Object getDailyAttendance();
    Attendance getAttendanceByDate(String username, LocalDate yesterday);
    void resetCheckout(String username, LocalDate yesterday);
    List<Attendance> getUsersWithMissedCheckoutYesterday();
long calculateDuration(Attendance attendance);    boolean resetDailyAttendance(String username);
    boolean resetCheckoutTime(String username);
    boolean resetCheckoutTimeForDate(String username, LocalDate resetDate);
    List<Attendance> getAttendanceRecordsForUserAndDate(Long id, LocalDate effectiveDate);
}