// package com.XAMMER.HRMS.controller;

// import com.XAMMER.HRMS.model.Attendance;
// import com.XAMMER.HRMS.service.AttendanceService;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.web.csrf.CsrfToken;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.security.Principal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// @Slf4j
// @Controller
// public class AuthController {
    

//     @Autowired
//     private AttendanceService attendanceService;

//     @GetMapping("/custom-login")
//     public String loginPage() {
//         return "login";
//     }

//     @GetMapping("/dashboard")
//     public String dashboard(Model model, Principal principal) {
//         String username = principal.getName();
//         Attendance attendance = attendanceService.getTodayAttendance(username);

//         model.addAttribute("username", username);
//         model.addAttribute("todayDate",
//                 LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy • hh:mm a")));

//         model.addAttribute("attendance", attendance);
//         model.addAttribute("hasCheckedIn", attendance != null && attendance.getCheckInTime() != null);
//         model.addAttribute("hasCheckedOut", attendance != null && attendance.getCheckOutTime() != null);
//         model.addAttribute("checkInTime", attendance != null ? attendance.getCheckInTime() : null);
//         model.addAttribute("checkOutTime", attendance != null ? attendance.getCheckOutTime() : null);

//         return "dashboard";
//     }

//     @PostMapping("/dashboard/checkin")
//     public String checkIn(Principal principal) {
//         String username = principal.getName();
//         attendanceService.checkIn(username);
//         return "redirect:/dashboard";
//     }

//     @PostMapping("/dashboard/checkout")
//     public String checkOut(Principal principal) {
//         String username = principal.getName();
//         attendanceService.checkOut(username);
//         return "redirect:/dashboard";
//     }

//     @ModelAttribute
//     public void addCsrfToken(Model model, HttpServletRequest request) {
//         CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//         model.addAttribute("_csrf", token);
//     }
// }
