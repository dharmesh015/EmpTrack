
package com.ecom.service;

import java.io.ByteArrayOutputStream;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.entity.ApiResponse;
import com.ecom.proxy.UserProxy;

public interface UserService {
	ApiResponse registerNewUser(UserProxy user);

	void updateUserRole(String userName, String roleId);

	ApiResponse saveProfileImage(String user, MultipartFile file);

	ResponseEntity<Resource> getImage(String imageUuid);
	
	 ByteArrayOutputStream exportToExcel();
}
