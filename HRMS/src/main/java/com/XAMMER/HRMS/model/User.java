package com.XAMMER.HRMS.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
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

    private String roles; // To store the user's role (e.g., "USER", "ADMIN")

    private LocalDate birthDate; // Added field for birth date

    @OneToMany(mappedBy = "user")
    private List<Attendance> attendances;

    // Constructors
    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String email, String role, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = role;
        this.birthDate = birthDate;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRoles() {
        System.out.println("Inside User.getRoles(): " + this.roles); // ADD THIS LINE
        return roles; // Return the 'role' field
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }
}