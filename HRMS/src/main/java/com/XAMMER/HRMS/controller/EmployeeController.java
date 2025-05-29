package com.XAMMER.HRMS.controller;

import com.XAMMER.HRMS.dto.PersonalDetailsUpdateDTO; // You need to create this DTO
import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import this
import org.springframework.web.bind.annotation.PostMapping;  // Import this
import org.springframework.web.bind.annotation.RequestBody;  // Import this
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody; // If @Controller is used for HTML and JSON
import java.util.HashMap; // Make sure this import is present

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map; // For simple JSON responses

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final UserService userService;
    // Formatter for displaying dates in "Month dd, yyyy" format
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    // Formatter for parsing dates from "yyyy-MM-dd" format (from <input type="date">)
    private final DateTimeFormatter inputDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;


    @Autowired
    public EmployeeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User employee = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee profile not found for username: " + username));

        String birthDateFormatted = null;
        if (employee.getBirthDate() != null) {
            birthDateFormatted = employee.getBirthDate().format(displayDateFormatter);
        }

        String joiningDateFormatted = null;
        if (employee.getJoiningDate() != null) {
            joiningDateFormatted = employee.getJoiningDate().format(displayDateFormatter);
        }

        model.addAttribute("birthDateFormatted", birthDateFormatted);
        model.addAttribute("joiningDateFormatted", joiningDateFormatted);
        model.addAttribute("employee", employee);
        // If CSRF is enabled, Spring Security + Thymeleaf usually handle adding _csrf automatically.
        // If not, you might need to add it to the model explicitly for AJAX POSTs if not using form submissions.
        return "employee_profile";
    }

    // **NEW METHOD TO HANDLE PROFILE UPDATES**
    @PostMapping("/profile/update-personal/{id}")
    @ResponseBody
    public ResponseEntity<?> updatePersonalDetails(
            @PathVariable Long id,
            @RequestBody PersonalDetailsUpdateDTO detailsUpdateDTO,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User userToUpdate = userService.findById(id)
            .orElse(null);

        if (userToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("message", "Employee not found with ID: " + id));
        }

        if (!userToUpdate.getUsername().equals(currentUsername) /* && !isCurrentUserAdmin(authentication) */) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(Map.of("message", "You are not authorized to update this profile."));
        }

        try {
            User updatedUser = userService.updatePersonalDetails(id, detailsUpdateDTO);

            // Construct the updatedEmployee map carefully, allowing for nulls
            Map<String, Object> updatedEmployeeMap = new HashMap<>();
            updatedEmployeeMap.put("dateOfBirth", updatedUser.getBirthDate() != null ? updatedUser.getBirthDate().format(inputDateFormatter) : null);
            updatedEmployeeMap.put("gender", updatedUser.getGender());
            updatedEmployeeMap.put("email", updatedUser.getEmail());
            updatedEmployeeMap.put("phone", updatedUser.getPhone());
            updatedEmployeeMap.put("address", updatedUser.getAddress());
            // Add employmentType and workLocation if they are part of the DTO and response
            // updatedEmployeeMap.put("employmentType", updatedUser.getEmploymentType());
            // updatedEmployeeMap.put("workLocation", updatedUser.getWorkLocation());


            return ResponseEntity.ok(Map.of(
                "message", "Personal details updated successfully!",
                "updatedEmployee", updatedEmployeeMap // Use the HashMap here
            ));

        } catch (DateTimeParseException e) {
            System.err.println("----------- DATETIME PARSE EXCEPTION IN EmployeeController -----------");
            e.printStackTrace(System.err);
            System.err.println("--------------------------------------------------------------------");
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid date format. Please use yyyy-MM-dd. Details: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("----------- ILLEGAL ARGUMENT EXCEPTION IN EmployeeController -----------");
            e.printStackTrace(System.err);
            System.err.println("----------------------------------------------------------------------");
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("############################################################################");
            System.err.println("UNEXPECTED EXCEPTION CAUGHT IN EmployeeController.updatePersonalDetails FOR ID: " + id);
            System.err.println("Exception Type: " + e.getClass().getName());
            System.err.println("Exception Message: " + e.getMessage());
            System.err.println("FULL STACK TRACE:");
            e.printStackTrace(System.err);
            System.err.println("############################################################################");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }
    }
