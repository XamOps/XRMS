package com.XAMMER.HRMS.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private Roles roles; // Using your Roles enum

    private String team; // e.g., "DevOps", "Business"

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager; // Self-referencing foreign key for the manager

    private int totalLeaves = 20; // Default annual quota
    private int leavesTaken = 0;

    private LocalDate birthDate;

    @OneToMany(mappedBy = "user")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "user")
    private List<LeaveRequest> leaveRequests; // Assuming you have a LeaveRequest entity

    @OneToMany(mappedBy = "manager")
    private List<User> subordinates; // To easily find direct reports of a manager

    public void setCheckOutTime(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCheckOutTime'");
    }

    public String getAttendanceDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttendanceDate'");
    }

    public String getCheckInTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCheckInTime'");
    }

    // Lombok's @Data will generate getFirstName(), setFirstName(),
    // getLastName(), setLastName(), and other standard getters and setters.
}