package com.XAMMER.HRMS.dto;

// Add validation annotations if desired (e.g., @NotBlank, @Email)
public class PersonalDetailsUpdateDTO {
    private String dateOfBirth; // Expects "yyyy-MM-dd" from frontend input type="date"
    private String gender;
    private String email;
    private String phone;
    private String address;

    // Getters and Setters
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}