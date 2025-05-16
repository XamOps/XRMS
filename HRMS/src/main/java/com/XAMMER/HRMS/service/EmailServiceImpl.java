package com.XAMMER.HRMS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private String fromAddress = "xrms@xammer.in"; // Replace with your email

    @Override
    public void sendLeaveRequestNotification(String to, String requesterName, LocalDate startDate, LocalDate endDate, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("New Leave Request from " + requesterName);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedStartDate = startDate.format(dateFormatter);
        String formattedEndDate = endDate.format(dateFormatter);
        message.setText("A new leave request has been submitted by " + requesterName + " for the period from " + formattedStartDate + " to " + formattedEndDate + " for the following reason: " + reason + "." + "\n\n" + "Please review it in the dashboard.");
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

    // Overloaded method with LocalDate for appliedDate - Consider if this is truly needed
    // and what information 'appliedDate' provides that startDate/endDate don't.
    @Override
    public void sendLeaveRequestNotification(String to, String requesterName, LocalDate appliedDate, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("New Leave Request from " + requesterName);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedAppliedDate = appliedDate.format(dateFormatter);
        message.setText("A new leave request has been submitted by " + requesterName + " on " + formattedAppliedDate + " for the following reason: " + reason + "." + "\n\n" + "Please review it in the dashboard.");
        mailSender.send(message);
    }

    @Override
    public void sendLeaveRequestCancellationNotification(String to, String requesterName, LocalDate startDate, LocalDate endDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("Leave Request Cancelled by " + requesterName);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedStartDate = startDate.format(dateFormatter);
        String formattedEndDate = endDate.format(dateFormatter);
        message.setText("The leave request submitted by " + requesterName + " for the period from " + formattedStartDate + " to " + formattedEndDate + " has been cancelled by the user.");
        mailSender.send(message);
    }

    @Override
    public void sendLeaveRequestCancellationNotificationToApprover(String to, String requesterName, LocalDate startDate, LocalDate endDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromAddress);
        message.setSubject("Leave Request Cancelled by " + requesterName);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedStartDate = startDate.format(dateFormatter);
        String formattedEndDate = endDate.format(dateFormatter);
        message.setText("The leave request submitted by " + requesterName + " for the period from " + formattedStartDate + " to " + formattedEndDate + " has been cancelled.");
        mailSender.send(message);
    }

    @Override
    public void sendLeaveRequestNotification(String to, String requesterName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendLeaveRequestNotification'");
    }
}