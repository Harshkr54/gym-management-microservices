package com.member_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Async // CRUCIAL: This pushes the email task to a background thread
    public void sendEmail(String to, String subject, String message){
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(to);
        sm.setSubject(subject);
        sm.setText(message);

        mailSender.send(sm);
        System.out.println("Email sent successfully to : " + to);
    }
}