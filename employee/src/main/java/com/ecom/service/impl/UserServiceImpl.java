package com.ecom.service.impl;

import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

//	public String registerNewUser(UserProxy usero) {
//		System.out.println("username--" + usero.getUserName());
//		if (userDao.findByUserName(usero.getUserName()).isPresent()) {
//			System.err.println("User NameExist");
//			return "UserNameExist";
//		}
//		if (userDao.findByEmail(usero.getEmail()).isPresent()) {
//			System.err.println("EmailExist");
//			return "EmailExist";
//		}
//
//		usero.setId(null);
//
//		User user = mapper.convertValue(usero, User.class);
//		try {
//			// Convert using the exact case that matches your enum
//			Gender gender = Gender.valueOf(usero.getGender().toUpperCase());
//			System.out.println("Converted gender: " + gender);
//			user.setGender(gender);
//		} catch (IllegalArgumentException e) {
//			System.err.println("Invalid gender value: " + usero.getGender());
//			return "InvalidGender";
//		}
//		user.setPassword(getEncodedPassword(user.getPassword()));
//
//		User savedUser = userDao.save(user);
//
//		Role role = roleDao.findById("USER").orElseThrow(() -> new RuntimeException("Role not found"));
//		Set<Role> userRoles = new HashSet<>();
//		userRoles.add(role);
//		savedUser.setRole(userRoles);
//
//		User userobj = userDao.save(savedUser);
//		return "register";
//	}

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
}