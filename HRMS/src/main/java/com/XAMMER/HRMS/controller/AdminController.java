package com.XAMMER.HRMS.controller;

import com.XAMMER.HRMS.dto.AttendanceResetRequest;
import com.XAMMER.HRMS.dto.ApiResponse;
import com.XAMMER.HRMS.dto.AttendanceDTO;
import com.XAMMER.HRMS.model.Attendance;
import com.XAMMER.HRMS.model.LeaveRequest;
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.AdminService;
import com.XAMMER.HRMS.service.AttendanceService;
import com.XAMMER.HRMS.service.LeaveRequestService;
import com.XAMMER.HRMS.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private LeaveRequestService leaveRequestService;

    // Admin dashboard (initial load or default view)
    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(value = "date", required = false) String dateStr,
                                 @RequestParam(value = "user", required = false) String username,
                                 Model model,
                                 Principal principal) {
        userService.findByUsername(principal.getName())
                .ifPresent(admin -> model.addAttribute("username", admin.getUsername()));

        model.addAttribute("todayDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd • hh:mm a")));

        model.addAttribute("dashboardMetrics", adminService.getDashboardMetrics());
        model.addAttribute("recentActivity", adminService.getRecentActivity());

        // Handle date and user filtering for attendance
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

        @GetMapping("/users/suggestions")
    @ResponseBody
    public ResponseEntity<List<String>> getUserSuggestions(@RequestParam("query") String query) {
        List<String> suggestions = userService.findUsernamesContaining(query);
        return ResponseEntity.ok(suggestions);
    }


    // Handler for resetting checkout time
    @PostMapping("/attendance/reset/checkout/{username}")
    public String resetCheckout(@PathVariable String username,
                                @RequestParam("resetDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate resetDate,
                                RedirectAttributes redirectAttributes) {
        logger.info("Admin requested to reset checkout for user: {} on date: {}", username, resetDate);
        boolean success = attendanceService.resetCheckoutTimeForDate(username, resetDate);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Checkout time reset successfully for " + username + " on " + resetDate);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reset checkout time for " + username + " on " + resetDate);
        }
        return "redirect:/admin/dashboard";
    }

    // Handler for resetting the entire day's attendance
    @PostMapping("/attendance/reset/checkin/{username}")
    public String resetToday(@PathVariable String username, RedirectAttributes redirectAttributes) {
        logger.warn("Admin requested to reset entire day's attendance for user: {}", username);
        boolean success = attendanceService.resetDailyAttendance(username);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Today's attendance reset successfully for " + username);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reset today's attendance for " + username);
        }
        return "redirect:/admin/dashboard";
    }

    // View pending leave requests for admin approval
    @GetMapping("/requests/pending")
    public String viewPendingAdminRequests(Model model) {
        List<LeaveRequest> pendingRequests = leaveRequestService.getAllPendingRequests();
        model.addAttribute("pendingRequests", pendingRequests);
        return "admin-pending-requests";
    }

    // Admin approves a leave request
    @PostMapping("/requests/approve")
    public String approveLeaveRequestByAdmin(@RequestParam("requestId") Long requestId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        userService.findByUsername(username).ifPresent(admin -> {
            LeaveRequest request = leaveRequestService.getLeaveRequestById(requestId);
            if (request != null && request.getLeaveStatus() == LeaveRequest.LeaveStatus.PENDING_ADMIN && request.getManagerApprovalStatus().equals(LeaveRequest.ApprovalStatus.APPROVED)) {
                leaveRequestService.approveLeaveRequestByAdmin(requestId, admin);
                redirectAttributes.addFlashAttribute("successMessage", "Leave request approved successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid request or not pending admin approval.");
            }
        });
        return "redirect:/admin/requests/pending";
    }

    // Admin rejects a leave request
    @PostMapping("/requests/reject")
    public String rejectLeaveRequestByAdmin(@RequestParam("requestId") Long requestId, @RequestParam("rejectionReason") String rejectionReason, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        userService.findByUsername(username).ifPresent(admin -> {
            LeaveRequest request = leaveRequestService.getLeaveRequestById(requestId);
            if (request != null && request.getLeaveStatus() == LeaveRequest.LeaveStatus.PENDING_ADMIN && request.getManagerApprovalStatus().equals(LeaveRequest.ApprovalStatus.APPROVED)) {
                leaveRequestService.rejectLeaveRequestByAdmin(requestId, admin, rejectionReason);
                redirectAttributes.addFlashAttribute("successMessage", "Leave request rejected successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid request or not pending admin approval.");
            }
        });
        return "redirect:/admin/requests/pending";
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        if (attendance.getUser() != null) {
            dto.setUsername(attendance.getUser().getUsername());
        }
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        logger.info("Converting Attendance for user: {}, date: {}", attendance.getUser() != null ? attendance.getUser().getUsername() : "N/A", attendance.getAttendanceDate());
        return dto;
    }
}