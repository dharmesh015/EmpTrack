package com.ecom.proxy;

import java.time.LocalDate;
import java.util.Set;

import com.ecom.configuration.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Column(unique = true)
	private String userName;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	@NotBlank(message = "email is required")
	private String email;

	@NotNull(message = "Gender is required")
//	@Enumerated(EnumType.STRING)
	private String gender;

	@NotBlank(message = "Address is required")
	private String address;

//	@Lob
//	@Column(name = "profile_image", columnDefinition = "BLOB")
//	private byte[] profileImage;

	private String imageUuid;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
	private String contactNumber;

	@NotBlank(message = "Pin code is required")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pin code must be 6 digits")
	private String pinCode;

	private Set<RoleProxy> role;

}
