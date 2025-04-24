package com.ecom.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.proxy.UserProxy;

public interface UserService {
	String registerNewUser(UserProxy user);

	void updateUserRole(String userName, String role);
	
	String saveProfileImage(String user,MultipartFile file);

	ResponseEntity<Resource> getimage(String imageUuid);
}
