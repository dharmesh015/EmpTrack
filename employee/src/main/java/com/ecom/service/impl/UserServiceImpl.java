package com.ecom.service.impl;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.EmailService;
import com.ecom.service.UserService;
import com.ecom.util.MapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MapperUtil mapper;

	@Autowired
	private EmailService emailservice;

	@Autowired
	private ObjectMapper objectMapper;

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public void updateUserRole(String userName, String roleobj) {
		User byUserName = userDao.findByUserName(userName).get();
		Role role = roleDao.findById(roleobj).orElseThrow(() -> new RuntimeException("Role not found"));
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		byUserName.setRole(userRoles);

		userDao.save(byUserName);

	}

	@Override
	public String registerNewUser(UserProxy usero) {
		System.out.println("username--" + usero.getUserName());
		if (userDao.findByUserName(usero.getUserName()).isPresent()) {
			System.err.println("User NameExist");
			return "UserNameExist";
		}
		if (userDao.findByEmail(usero.getEmail()).isPresent()) {
			System.err.println("EmailExist");
			return "EmailExist";
		}
		usero.setId(null);
		User user = mapper.convertValue(usero, User.class);
		try {
			// Convert using the exact case that matches your enum
			Gender gender = Gender.valueOf(usero.getGender().toUpperCase());
			System.out.println("Converted gender: " + gender);
			user.setGender(gender);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid gender value: " + usero.getGender());
			return "InvalidGender";
		}

		// Set the image UUID if available
		if (usero.getImageUuid() != null && !usero.getImageUuid().isEmpty()) {
			user.setImageUuid(usero.getImageUuid());
		}

		user.setPassword(getEncodedPassword(user.getPassword()));
		User savedUser = userDao.save(user);
		Role role = roleDao.findById("USER").orElseThrow(() -> new RuntimeException("Role not found"));
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		savedUser.setRole(userRoles);
		User userobj = userDao.save(savedUser);
		return "register";
	}

	@Override
	public String saveProfileImage(String userData, MultipartFile file) {

		try {

			UserProxy userProxy = objectMapper.readValue(userData, UserProxy.class);

			if (file != null && !file.isEmpty()) {
				
				String uuid = UUID.randomUUID().toString();
				String originalFilename = file.getOriginalFilename();

				// Get file extension
				String extension = "";
				if (originalFilename != null && originalFilename.contains(".")) {
					extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				}

				// Create the filename with UUID
				String newFilename = uuid + extension;

				// Get the path to save the file
				String uploadDir = null;
				try {
					uploadDir = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
							+ File.separator + "documents";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Create directories if they don't exist
				File directory = new File(uploadDir);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// Save the file
				String filePath = uploadDir + File.separator + newFilename;
				try {
					Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userProxy.setImageUuid(newFilename);
			}

			return registerNewUser(userProxy);

		} catch (IOException e) {
			e.printStackTrace();
			return "Invalid or expired token";

		}

		
		
	}

	@Override
	public ResponseEntity<Resource> getimage(String imageUuid) {
		try {
			String imagePath = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
					+ File.separator + "documents" + File.separator + imageUuid;

			Resource resource = new UrlResource(Paths.get(imagePath).toUri());

			if (resource.exists()) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageUuid + "\"")
						.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}