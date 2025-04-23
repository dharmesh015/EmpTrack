
package com.ecom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

import com.ecom.configuration.enums.Gender;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
 
	@Column(nullable = false)
	private String name;
 
	@Column(nullable = false)
	private LocalDate dob;
 
	@Column(nullable = false, unique = true)
	private String userName;
 
	@Column(nullable = false)
	private String password;
	
	
	@Column(nullable = false)
	private String email;
	

	@Column(nullable = false)
	private String gender;
 
	@Column(nullable = false)
	private String address;
 
	@Column(name = "profile_image")
	private  byte[] profileImage;
 
	@Column(name = "contact_number", nullable = false)
	private String contactNumber;
 
	@Column(name = "pin_code", nullable = false)
	private String pinCode;
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL, CascadeType.REMOVE })
	@JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "ROLE_ID") })
	private Set<Role> role;
	
	

	
}
