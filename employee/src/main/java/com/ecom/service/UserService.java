package com.ecom.service;

import com.ecom.proxy.UserProxy;

public interface UserService {
	String registerNewUser(UserProxy user);

	void updateUserRole(String userName, String role);
}
