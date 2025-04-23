package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.RoleProxy;
import com.ecom.service.RoleService;

@RestController
public class RoleController {

	@Autowired
	private RoleService roleService;

	@PostMapping("/createNewRole")
	public RoleProxy createNewRole(@RequestBody RoleProxy role) {
		return roleService.createNewRole(role);
	}

}
