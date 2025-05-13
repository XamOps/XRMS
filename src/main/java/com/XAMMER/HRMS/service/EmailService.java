package com.XAMMER.HRMS.service;

public interface EmailService {
    void sendLeaveRequestNotification(String to, String requesterName);
    void sendLeaveRequestApprovalNotification(String to, String requesterName);
    void sendLeaveRequestRejectionNotification(String to, String requesterName, String reason);
}