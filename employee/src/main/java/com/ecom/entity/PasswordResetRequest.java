package com.ecom.entity;

import lombok.Data;

@Data
public class PasswordResetRequest {

	private String email;
	private String newPassword;

}
