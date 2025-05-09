package com.XAMMER.HRMS.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public class AttendanceDTO {
    private String username;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    // ✅ Add this method to fix the Thymeleaf error
    public String getDuration() {
        if (checkInTime != null && checkOutTime != null) {
            Duration duration = Duration.between(checkInTime, checkOutTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            return String.format("%02d:%02d", hours, minutes);
        }
        return "-";
    }
}
