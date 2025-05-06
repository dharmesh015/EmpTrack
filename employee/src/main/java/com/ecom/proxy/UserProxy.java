
package com.ecom.proxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.ecom.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserProxy {
	private Long id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Date of birth is required")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dob;

	@NotBlank(message = "Username is required")
	@Size(min = 3, message = "Username must be at least 3 characters long")
	@Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
	private String userName;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase, one uppercase, one special character, and no spaces")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

	@NotNull(message = "Gender is required")
	private String gender;
	@NotBlank(message = "Address is required")
	@Size(min = 5, message = "Address must be at least 5 characters long")
	private String address;

	private String imageUuid;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
	private String contactNumber;

	@NotBlank(message = "Pin code is required")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pin code must be 6 digits")
	private String pinCode;
	
	private boolean active;

	private Set<RoleProxy> role;


	private Role nrole;

	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}