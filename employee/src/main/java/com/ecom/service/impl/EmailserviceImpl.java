package com.ecom.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.dao.UserDao;
import com.ecom.entity.User;
import com.ecom.service.EmailService;
import com.ecom.service.TokenService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Service
public class EmailserviceImpl implements EmailService {
    @Autowired
    private UserDao userDao;
    
    @Value("${spring.mail.username}")
    private String sender;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired(required = false)
    private HttpSession session;
    
    @Override
    public void sendTestEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo("example@example.com");
        message.setSubject("Test email from Spring Boot");
        message.setText("This is a test email from your application");
        mailSender.send(message);
    }
    
    @Override
    public String sendPasswordResetEmail(String toEmail) {
        try {
           
            // Find user by email
            Optional<User> userObj = userDao.findByEmail(toEmail);
            
            if (userObj.isEmpty()) {
                return "UNF"; // User Not Found
            }
            
            User user = userObj.get();
            
            // Generate password reset token
            String resetToken = tokenService.generatePasswordResetToken(user.getEmail(), user.getPassword());
            String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
            
            // Create and send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("Hello " + user.getName() + ",\n\n" +
                    "You requested to reset your password. Please click the link below to reset your password:\n\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 10 minutes.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Thank you,\nYour Application Team");
            
            mailSender.send(message);
            return "S"; // Success
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    @Override
    public boolean resetPassword(String email, String newPassword) {
        try {
            Optional<User> userObj = userDao.findByEmail(email);
            
            if (userObj.isEmpty()) {
                return false;
            }
            
            User user = userObj.get();
            user.setPassword(getEncodedPassword(newPassword));
            userDao.save(user);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}