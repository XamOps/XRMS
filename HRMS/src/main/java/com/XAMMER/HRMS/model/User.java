package com.XAMMER.HRMS.model;

import java.time.LocalDate;
import java.util.ArrayList; // Import ArrayList
import java.util.Collection;
import java.util.List;
import jakarta.persistence.CascadeType; // Import CascadeType
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
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private Roles roles;

    private String team;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    private int totalLeaves = 20;
    private int leavesTaken = 0;

    private LocalDate birthDate;

    private String employeeId;
    private String phone;
    private String address;
    private LocalDate joiningDate;
    private String department;
    private String jobTitle;
    private String gender;
    private String maritalStatus;
    private String profilePictureUrl;

    // *** CRITICAL CHANGES HERE ***
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>(); // Initialize the list to avoid NullPointerException

    // *** CRITICAL CHANGES HERE ***
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveRequest> leaveRequests = new ArrayList<>(); // Initialize the list to avoid NullPointerException

    @OneToMany(mappedBy = "manager") // No cascade on delete for subordinates, as they are not "owned" and should not be deleted with the manager.
    private List<User> subordinates = new ArrayList<>(); // Initialize the list

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(roles.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

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
}