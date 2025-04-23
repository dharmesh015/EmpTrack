package com.ecom.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import org.springframework.mail.javamail.MimeMessageHelper;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.dao.UserDao;

import com.ecom.entity.User;
import com.ecom.service.EmailService;
import com.ecom.service.TokenService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailserviceImpl implements EmailService {
	@Autowired
	private UserDao userdao;

	@Value("${spring.mail.username}")
	private String sender;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenService tokenService;

	public void sendTestEmail() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(sender);
		message.setTo("dharmeshgelatardhirajlal@gmail.com");
		message.setSubject("Test email from Spring Boot");
		message.setText("This is a test email from your application");
		mailSender.send(message);
	}

	public String sendPasswordResetEmail(String toEmail) {

		User user = userdao.findByEmail(toEmail);
		if (user == null) {
			return "UNF";
		}

		String resetToken = tokenService.generatePasswordResetToken(user.getEmail(), user.getPassword());
		String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(sender);
		message.setTo(toEmail);
		message.setSubject("Password Reset Request");
		message.setText("Hello " + user.getName() + ",\n\n"
				+ "You requested to reset your password. Please click the link below to reset your password:\n\n"
				+ resetLink + "\n\n" + "This link will expire in 10 minutes.\n\n"
				+ "If you did not request a password reset, please ignore this email.\n\n"
				+ "Thank you,\nYour Application Team");

		System.out.println("Hello " + user.getName() + ",\n\n"
				+ "You requested to reset your password. Please click the link below to reset your password:\n\n"
				+ resetLink + "\n\n" + "This link will expire in 10 minutes.\n\n"
				+ "If you did not request a password reset, please ignore this email.\n\n"
				+ "Thank you,\nYour Application Team");

		mailSender.send(message);
		return "S";

	}

	public boolean resetPassword(String email, String newPassword) {
		try {
			User user = userdao.findByEmail(email);
			if (user == null) {
				System.err.println("user not found");
				return false;
			}

			user.setPassword(getEncodedPassword(newPassword));
			userdao.save(user);
			System.err.println("user  found");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	


	

	
}