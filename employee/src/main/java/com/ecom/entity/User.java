
package com.ecom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
	@Enumerated(EnumType.ORDINAL)
	private Gender gender;
	
	@Column(nullable = false)
	private String address;
	
	private String imageUuid;
	
	@Column(name = "contact_number", nullable = false)
	private String contactNumber;
	
	@Column(name = "pin_code", nullable = false)
	private String pinCode;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL, CascadeType.REMOVE })
	@JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "ROLE_ID") })
	private Set<Role> role;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;



	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "nrole_id", nullable = false)
	private Role nrole;
}