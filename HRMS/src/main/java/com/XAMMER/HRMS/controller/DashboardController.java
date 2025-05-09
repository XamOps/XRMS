package com.XAMMER.HRMS.controller;


import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.Holiday;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.AttendanceService;
import com.XAMMER.HRMS.service.HolidayService;
import com.XAMMER.HRMS.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class DashboardController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService; // Autowire UserService

    @Autowired
    private HolidayService holidayService; // Autowire HolidayService

    // ✅ Custom Login Page
    @GetMapping("/custom-login")
    public String loginPage() {
        return "login"; // Must match src/main/resources/templates/login.html
    }

    // ✅ Dashboard View
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/custom-login"; // Redirect if not authenticated
        }
        String username = authentication.getName();
        model.addAttribute("username", username);

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd,yyyy"); // Corrected date pattern to include year
        model.addAttribute("todayDate", today.format(dateFormatter));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // Format for 12-hour time with AM/PM
        model.addAttribute("currentTime", now.format(timeFormatter));

        Attendance todayAttendance = attendanceService.getTodayAttendance(username);

        if (todayAttendance != null && todayAttendance.getCheckOutTime() == null) {
            // Currently checked in
            model.addAttribute("checkInTime", todayAttendance.getCheckInTime());
            model.addAttribute("checkOutTime", null);
        } else {
            // Not checked in OR already checked out
            model.addAttribute("checkInTime", null);
            model.addAttribute("checkOutTime", null);
        }
        List<User> upcomingBirthdays = userService.getUpcomingBirthdays();
        model.addAttribute("upcomingBirthdays", upcomingBirthdays);

        List<Holiday> upcomingHolidays = holidayService.getUpcomingHolidays(); // Now this should work
        model.addAttribute("upcomingHolidays", upcomingHolidays);

        return "dashboard";
    }



    @PostMapping("/dashboard/checkin")
    @ResponseBody
    public Map<String, Object> checkIn() {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("status", "error");
            response.put("message", "User not authenticated.");
            return response;
        }
        String username = authentication.getName();
        try {
            Attendance attendance = attendanceService.checkIn(username);
            response.put("status", "success");
            response.put("checkInTime", attendance.getCheckInTime().toString());
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Check-in failed. Try again.");
            log.error("Check-in error for user {}: {}", username, e.getMessage());
        }
        return response;
    }

    @PostMapping("/dashboard/checkout")
    @ResponseBody
    public Map<String, Object> checkOut() {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("status", "error");
            response.put("message", "User not authenticated.");
            return response;
        }
        String username = authentication.getName();
        try {
            Attendance attendance = attendanceService.checkOut(username);
            response.put("status", "success");
            response.put("checkOutTime", attendance.getCheckOutTime().toString());
            // No need to update the Attendance record here for re-check-in logic
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Check-out failed. Try again.");
            log.error("Check-out error for user {}: {}", username, e.getMessage());
        }
        return response;
    }
}