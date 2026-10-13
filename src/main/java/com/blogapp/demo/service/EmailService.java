package com.blogapp.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestClient restClient = RestClient.create();

    public void sendWelcomeEmail(String email, String username) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            String html = templateEngine.process("email/welcome", context);

            Map<String, Object> body = Map.of(
                "sender", Map.of("email", fromEmail, "name", "TrueBook"),
                "to", List.of(Map.of("email", email)),
                "subject", "Welcome To TrueBook",
                "htmlContent", html
            );

            restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", brevoApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
