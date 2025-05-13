package com.XAMMER.HRMS.service;

import com.XAMMER.HRMS.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private String fromAddress = "akshayarora0172@gmail.com"; // Replace with your email

    @Override
    public void sendLeaveRequestNotification(String to, String requesterName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("New Leave Request from " + requesterName);
        message.setText("A new leave request has been submitted by " + requesterName + ". Please review it in the dashboard.");
        mailSender.send(message);
    }

    @Override
    public void sendLeaveRequestApprovalNotification(String to, String requesterName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("Your Leave Request has been Approved");
        message.setText("Your leave request has been approved. Please check your dashboard for details.");
        mailSender.send(message);
    }

    @Override
    public void sendLeaveRequestRejectionNotification(String to, String requesterName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("Your Leave Request has been Rejected");
        message.setText("Your leave request has been rejected with the following reason: " + reason + ". Please check your dashboard for details.");
        mailSender.send(message);
    }
}