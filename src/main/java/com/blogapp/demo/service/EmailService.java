package com.blogapp.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendWelcomeEmail(String email,String username) {

        try {
            Context context=new Context();
            context.setVariable("username", username);
            String html= templateEngine.process("email/welcome", context);
            MimeMessage message= mailSender.createMimeMessage();
            MimeMessageHelper helper= new MimeMessageHelper(message,true);

            helper.setTo(email);
            helper.setSubject("Welcome To TrueBook");
            helper.setText(html,true);

            mailSender.send(message);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}