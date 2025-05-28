package com.XAMMER.HRMS.controller;

import com.XAMMER.HRMS.model.User;
import com.XAMMER.HRMS.service.UserService;
import com.XAMMER.HRMS.dto.UserDTO; // Import your UserDTO
import com.XAMMER.HRMS.model.Roles; // Keep if still relevant for setting roles
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Make sure this is imported

@Controller
@RequestMapping("/admin/employees")
public class AdminEmployeeController {

    private final UserService userService;

    @Autowired
    public AdminEmployeeController(UserService userService) {
        this.userService = userService;
    }

    

    /**
     * Handles GET requests to /admin/employees to render the employees.html page.
     * This might still pass User entities to the Thymeleaf template,
     * but if the template itself tries to access lazy collections, you'll need to adapt.
     * For now, let's assume the template only needs basic user info or handles eager loading separately.
     */
    @GetMapping
    public String showEmployeeManagementPage(Model model) {
        // Option 1: If your Thymeleaf template only needs basic fields (not lazy collections)
        // model.addAttribute("employees", userService.getAllUsers());

        // Option 2 (Recommended for consistency with API): Pass DTOs to the model
        // If your frontend JavaScript relies on this initial load to populate a table,
        // it's better to pass DTOs here too. You'll need to adapt your Thymeleaf template
        // to expect UserDTO objects.
        model.addAttribute("employees", userService.findAllUsersDTO()); // Use the DTO method
        return "employee-admin";
    }

    // --- REST API Endpoints for your frontend JavaScript ---

    // Get all employees (users with employee roles) - NOW RETURNS UserDTO
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getAllEmployeesApi() {
        // Call the service method that returns DTOs
        List<UserDTO> employees = userService.findAllUsersDTO();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    // Add a new employee (which means creating a new User)
    // Client sends a User entity, but the response should be a DTO
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<UserDTO> addEmployeeApi(@RequestBody User user) {
        User newEmployee = userService.addUser(user); // addUser likely returns a User entity
        // Convert the returned User entity to a UserDTO before sending the response
        return new ResponseEntity<>(new UserDTO(newEmployee), HttpStatus.CREATED);
    }

    // Get a single employee by ID - NOW RETURNS UserDTO
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<UserDTO> getEmployeeApi(@PathVariable Long id) {
        // You'll need a service method that specifically returns a UserDTO by ID,
        // or convert the Optional<User> to Optional<UserDTO>.
        Optional<User> employeeOptional = Optional.empty(); // getUserById still returns User
        return employeeOptional.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Search and filter employees - NOW RETURNS UserDTO
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> searchAndFilterEmployeesApi(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String department) {
        // Assuming searchAndFilterUsers returns List<User>, convert it to List<UserDTO>
        List<User> employees = userService.searchAndFilterUsers(query, department);
        List<UserDTO> employeeDTOs = employees.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(employeeDTOs, HttpStatus.OK);
    }

    // Update an existing employee
    // Client sends a User entity, but the response should be a DTO
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<UserDTO> updateEmployeeApi(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedEmployee = userService.updateUser(id, user); // updateUser likely returns a User entity
            // Convert the returned User entity to a UserDTO before sending the response
            return new ResponseEntity<>(new UserDTO(updatedEmployee), HttpStatus.OK);
        } catch (RuntimeException e) {
            // Log the exception
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete an employee - No change needed as it returns Void
@DeleteMapping("/api/{id}")
@ResponseBody
public ResponseEntity<Void> deleteEmployeeApi(@PathVariable Long id) {
    try {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    } catch (EmptyResultDataAccessException ex) { // Catch specific exception
        System.err.println("Attempted to delete non-existent employee with ID: " + id);
        ex.printStackTrace(); // For debugging in server logs
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
    } catch (Exception e) { // Catch any other unexpected exceptions
        System.err.println("Error deleting employee with ID " + id + ": " + e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error for unexpected issues
    }
}

        @GetMapping("/api/suggestions") // New endpoint for search suggestions
    @ResponseBody
    public ResponseEntity<List<String>> getUserSuggestions(@RequestParam("query") String query) {
        // Assuming your UserService has a method to find user usernames by partial query
        List<String> usernames = userService.findUsernamesByQuery(query); // You'll need to implement this in UserService
        if (usernames.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(usernames, HttpStatus.OK);
    }
}