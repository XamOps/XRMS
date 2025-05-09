package com.XAMMER.HRMS.controller;

import com.XAMMER.HRMS.dto.AttendanceResetRequest;
import com.XAMMER.HRMS.dto.ApiResponse;
import com.XAMMER.HRMS.dto.AttendanceDTO;
import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.AdminService;
import com.XAMMER.HRMS.service.AttendanceService;
import com.XAMMER.HRMS.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    // Admin dashboard (initial load or default view)
    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(value = "date", required = false) String dateStr,
                                 @RequestParam(value = "user", required = false) String username,
                                 Model model,
                                 Principal principal) {
        userService.findByUsername(principal.getName())
                .ifPresent(admin -> model.addAttribute("username", admin.getFirstName()));

        model.addAttribute("todayDate", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy • hh:mm a"))
        );

        model.addAttribute("dashboardMetrics", adminService.getDashboardMetrics());
        model.addAttribute("recentActivity", adminService.getRecentActivity());

        // Handle date and user filtering
        LocalDate selectedDate = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();
        List<Attendance> filteredAttendance = attendanceService.getDailyAttendance(selectedDate);
        if (username != null && !username.isBlank()) {
            filteredAttendance = filteredAttendance.stream()
                    .filter(a -> a.getUser() != null && username.equalsIgnoreCase(a.getUser().getUsername()))
                    .collect(Collectors.toList());
        }

        List<AttendanceDTO> attendanceDTOs = filteredAttendance.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        model.addAttribute("attendances", attendanceDTOs);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedUser", username);
        model.addAttribute("allUsers", userService.getAllUsernames());

        return "dashboard-admin";
    }

    // API for AJAX or REST daily data
    @GetMapping("/attendance/daily")
    @ResponseBody
    public ResponseEntity<List<AttendanceDTO>> getDailyAttendance(@RequestParam(value = "date", required = false) LocalDate date) {
        if (date == null) date = LocalDate.now();
        List<Attendance> attendanceList = attendanceService.getDailyAttendance(date);
        List<AttendanceDTO> attendanceDTOs = attendanceList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }

    // Reset a user's checkout for a specific date (used by modal)
    @PostMapping("/attendance/reset/checkout/{username}")
    @ResponseBody
    public ResponseEntity<ApiResponse> resetCheckoutByUsernameAndDate(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate date) {
        try {
            if (date == null) date = LocalDate.now();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            adminService.resetAttendance(user.getId(), date);
            return ResponseEntity.ok(new ApiResponse("success", "Attendance reset successfully for user: " + username + " on " + date));
        } catch (IllegalArgumentException e) {
            logger.warn("Reset failed for username {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Reset failed for username {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("error", "Failed to reset attendance: " + e.getMessage()));
        }
    }

    // Reset ALL attendance records for a user
    @DeleteMapping("/attendance/reset/all/{username}")
    @ResponseBody
    public ResponseEntity<ApiResponse> resetAllAttendance(@PathVariable String username) {
        try {
            attendanceService.deleteAllAttendanceRecords(username);
            return ResponseEntity.ok(new ApiResponse("success", "All attendance records reset successfully for user: " + username));
        } catch (IllegalArgumentException e) {
            logger.warn("Reset all failed for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to reset all attendance for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("error", "Failed to reset all attendance: " + e.getMessage()));
        }
    }

    // Generic reset endpoint (currently unused in frontend, can be used by REST clients)
    @PostMapping("/attendance/reset")
    @ResponseBody
    public ResponseEntity<ApiResponse> resetAttendance(@RequestBody AttendanceResetRequest request) {
        try {
            if (request.getDate() == null) request.setDate(LocalDate.now());
            adminService.resetAttendance(request.getUserId(), request.getDate());
            return ResponseEntity.ok(new ApiResponse("success", "Attendance reset successfully for user ID: " + request.getUserId() + " on " + request.getDate()));
        } catch (Exception e) {
            logger.error("Reset attendance failed for user ID {}: {}", request.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("error", "Failed to reset attendance: " + e.getMessage()));
        }
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        if (attendance.getUser() != null) {
            dto.setUsername(attendance.getUser().getUsername());
        }
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        return dto;
    }
}
