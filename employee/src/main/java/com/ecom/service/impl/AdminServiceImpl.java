package com.ecom.service.impl;

import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecom.util.MapperUtil;
import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;

import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	private final MapperUtil mapperUtil;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	AdminServiceImpl(MapperUtil mapperUtil) {
		this.mapperUtil = mapperUtil;
	}

	public Page<UserProxy> getAllUsersPageWise(PageRequest pageable) {
		System.err.println("pagewise service");

		Page<User> allByRoleName = userDao.findByRole_RoleName("USER", pageable);
		System.out.println(allByRoleName);
		return new PageImpl<>(mapperUtil.convertList(allByRoleName.getContent(), UserProxy.class), pageable,
				allByRoleName.getTotalElements());

	}

	@Transactional
	public void deleteUser(String userName) {
		User user = userDao.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));

		user.getRole().clear();
		userDao.delete(user);

	}

	public String updateUser(UserProxy user) {
		System.out.println(user.getGender());
		User userobj = userDao.findByUserName(user.getUserName())
				.orElseThrow(() -> new RuntimeException("User  not found"));
		User newuser = mapperUtil.convertValue(user, User.class);

		userDao.save(newuser);
		return "saved";
	}

	public UserProxy getuser(String name) {
		Optional<User> userdata = userDao.findByUserName(name);
		return mapperUtil.convertValue(userdata.get(), UserProxy.class);
	}

	@Override
	public Page<User> searchUsers(String query, PageRequest pageable) {
		if (query == null || query.trim().isEmpty()) {
			return userDao.findAll(pageable);
		}

		return userDao.searchUsers(query, pageable);
	}

	@Override
	public String generateFakeUsers() {
		Faker faker = new Faker();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		for (int i = 1; i <= 105; i++) {
			User user = new User();

			user.setName(faker.name().fullName());

			user.setDob(faker.date().birthday().toInstant().atOffset(ZoneOffset.UTC).toLocalDate());

			user.setEmail(faker.internet().emailAddress());

			user.setUserName(faker.name().username() + i); // Ensure uniqueness

			user.setPassword(encoder.encode("Password@123")); // Default encrypted password

			user.setGender(faker.options().option(Gender.class));

			user.setAddress(faker.address().fullAddress());

			user.setImageUuid(null); // You could set a UUID for a profile image if needed

			user.setContactNumber(faker.phoneNumber().subscriberNumber(10));

			user.setPinCode(faker.address().zipCode());

			User savedUser = userDao.save(user);

			Role role = roleDao.findById("USER").orElseThrow(() -> new RuntimeException("Role not found"));
			Set<Role> userRoles = new HashSet<>();
			userRoles.add(role);
			savedUser.setRole(userRoles);
			User userobj = userDao.save(savedUser);

			userDao.save(user);
		}

		return "105 fake users added successfully.";

	}

}
