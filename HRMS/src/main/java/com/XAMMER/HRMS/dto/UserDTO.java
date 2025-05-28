// src/main/java/com/XAMMER/HRMS/dto/UserDTO.java
package com.XAMMER.HRMS.dto;

import com.XAMMER.HRMS.model.Roles;
import com.XAMMER.HRMS.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate birthDate;
    private String maritalStatus;
    private String jobTitle;
    private LocalDate joiningDate;
    private String username;
    private String role; // Changed: from role (String) to roles (Enum converted to String)

    // ADD THIS FIELD FOR PASSWORD INPUT FROM FRONTEND
    private String password;

    private String fullName; // This will be a derived field in DTO

    // For the manager, we'll only include basic info to avoid circular references
    private Long managerId;
    private String managerFullName;
    private String department;
    private Roles roles;

    // Constructor to map User entity to UserDTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.birthDate = user.getBirthDate();
        this.maritalStatus = user.getMaritalStatus();
        this.jobTitle = user.getJobTitle();
        this.joiningDate = user.getJoiningDate();
        this.password = user.getPassword();
        this.department = user.getDepartment();
        this.roles = user.getRoles();



        this.username = user.getUsername();
        if (user.getRoles() != null) {
            this.role = user.getRoles().name(); // Get the string representation of the enum
        }
        this.fullName = user.getFirstName() + " " + user.getLastName();

        // Handle manager mapping to avoid LazyInitializationException and circular dependency
        if (user.getManager() != null) {
            this.managerId = user.getManager().getId();
            this.managerFullName = user.getManager().getFirstName() + " " + user.getManager().getLastName();
        }
        // IMPORTANT: DO NOT set the password here when converting from User entity to UserDTO
        // this.password = user.getPassword(); // <-- NO! Passwords should not be sent back to frontend.
    }
}