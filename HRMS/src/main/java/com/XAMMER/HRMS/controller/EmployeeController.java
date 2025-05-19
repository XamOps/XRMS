package com.XAMMER.HRMS.controller;

import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final UserService userService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    @Autowired
    public EmployeeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User employee = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        String birthDateFormatted = null;
        if (employee.getBirthDate() != null) {
            birthDateFormatted = employee.getBirthDate().format(dateFormatter);
        }

        String joiningDateFormatted = null;
        if (employee.getJoiningDate() != null) {
            joiningDateFormatted = employee.getJoiningDate().format(dateFormatter);
        }

        model.addAttribute("birthDateFormatted", birthDateFormatted);
        model.addAttribute("joiningDateFormatted", joiningDateFormatted);
        model.addAttribute("employee", employee);
        return "employee_profile";
    }
}