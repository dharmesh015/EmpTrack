package com.ecom.service;

import org.springframework.stereotype.Service;



public interface EmailService {
	void sendTestEmail();

	String sendPasswordResetEmail(String toEmail);

	boolean resetPassword(String email, String newPassword);

	String getEncodedPassword(String password);

	

	

}
