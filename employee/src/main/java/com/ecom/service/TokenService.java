package com.ecom.service;

public interface TokenService {
	String generatePasswordResetToken(String email, String password);

	String validateToken(String token);

	String getEmail(String token);
}
