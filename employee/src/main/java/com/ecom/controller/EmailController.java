package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.dao.UserDao;
import com.ecom.entity.ApiResponse;
import com.ecom.entity.EmailRequest;
import com.ecom.service.EmailService;
import com.ecom.service.TokenService;

@RestController
@CrossOrigin
public class EmailController {
	@Autowired
	public EmailService emailService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserDao userDao;

	@PostMapping("/send-email")
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
		try {
			String result = emailService.sendPasswordResetEmail(request.getEmail());

			if (result.equals("UNF")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND.value(),
						"UserNotFound", "The email address you entered is not associated with any account."));
			} else if (result.equals("S")) {
				return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Success",
						"Password reset email has been sent successfully."));

			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error",
								"Failed to send password reset email: " + result));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "An error occurred: " + e.getMessage()));
		}
	}

	@GetMapping("/reset-password/{token}/{newPassword}")
	public ResponseEntity<?> resetPassword(@PathVariable("token") String token,
			@PathVariable("newPassword") String newPassword) {

		try {
			String email = tokenService.validateToken(token);
			if (email == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "InvalidToken",
								"The password reset link is invalid or has expired."));
			}

			boolean result = emailService.resetPassword(email, newPassword);
			if (result) {
				return ResponseEntity
						.ok(new ApiResponse(HttpStatus.OK.value(), "Success", "Password updated successfully"));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
						HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "Failed to update password"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "An error occurred: " + e.getMessage()));
		}
	}
}