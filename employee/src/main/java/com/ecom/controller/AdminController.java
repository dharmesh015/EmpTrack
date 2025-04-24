package com.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.ecom.entity.User;

import com.ecom.proxy.RoleProxy;
import com.ecom.proxy.UserProxy;
import com.ecom.service.AdminService;
import com.ecom.service.UserService;
import com.ecom.service.impl.AdminServiceImpl;
import com.ecom.util.MapperUtil;

@RestController
@CrossOrigin
public class AdminController {

	@Autowired
	private AdminService adminservice;

	@Autowired
	private UserService userService;

	@Autowired
	private MapperUtil mapper;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAllUsersPageWise")
	public Page<UserProxy> getAlluser(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		PageRequest pageable = PageRequest.of(page, size);
		return adminservice.getAllUsersPageWise(pageable);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteUser/{userName}")
	public ResponseEntity<Void> deleteUserByUserName(@PathVariable("userName") String userName) {
		
		adminservice.deleteUser(userName);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updateUser")
	public String updateUser(@RequestBody UserProxy userProxy) {
		return adminservice.updateUser(userProxy);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getuser/{name}")
	public UserProxy getUser(@PathVariable("name") String name) {
		return adminservice.getuser(name);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/searchUsers")
	public Page<User> searchUsers(
	        @RequestParam String query,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
	    PageRequest pageable = PageRequest.of(page, size);
	    return adminservice.searchUsers(query, pageable);
	}
	  @GetMapping("/generate-fake-users")
		public String generateFakeUsers() {
			String message = adminservice.generateFakeUsers();
			return message;
		}


	
}
