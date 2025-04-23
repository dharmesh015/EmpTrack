package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.dao.UserDao;
import com.ecom.entity.EmailRequest;
import com.ecom.entity.PasswordResetRequest;
import com.ecom.service.EmailService;
import com.ecom.service.TokenService;
import com.ecom.service.impl.EmailserviceImpl;

@RestController
@CrossOrigin
public class EmailController {

	@Autowired
	public EmailService emailService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserDao userdao;

	@PostMapping("/send-email")
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
		String result = emailService.sendPasswordResetEmail(request.getEmail());

		if (result.equals("UNF")) {
			return ResponseEntity.ok("UNF");
		} else if (result.equals("S")) {
			return ResponseEntity.ok("S");
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}

	

	@GetMapping("/reset-password/{token}/{newPassword}")
	public ResponseEntity<String> resetPassword(@PathVariable("token") String token,
			@PathVariable("newPassword") String newPassword) {

		String email = tokenService.validateToken(token);

		if (email == null) {
			return ResponseEntity.badRequest().body("Invalid or expired token");
		}

		boolean result = emailService.resetPassword(email, newPassword);

		if (result) {
			return ResponseEntity.ok("Password updated successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to update password");
		}

	}
}
