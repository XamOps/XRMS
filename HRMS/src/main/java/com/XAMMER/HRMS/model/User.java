package com.XAMMER.HRMS.model;

import java.time.LocalDate;
import java.util.Collection;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails { // Implement UserDetails
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

    // New fields from the reference image
    private String employeeId;
    private String phone;
    private String address;
    private LocalDate joiningDate;
    private String department;
    private String jobTitle;
    private String gender; // Consider using an Enum for this
    private String maritalStatus; // Consider using an Enum for this

    @OneToMany(mappedBy = "user")
    private List<Attendance> attendances; // Keep this if you've implemented attendance

    @OneToMany(mappedBy = "user")
    private List<LeaveRequest> leaveRequests; // Assuming you have a LeaveRequest entity

    @OneToMany(mappedBy = "manager")
    private List<User> subordinates; // To easily find direct reports of a manager

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(roles.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can implement logic based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can implement logic based on your requirements
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can implement logic based on your requirements
    }

    @Override
    public boolean isEnabled() {
        return true; // You can implement logic based on your requirements
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
        private String profilePictureUrl; // Add this field


    // Lombok's @Data will still generate other methods
}