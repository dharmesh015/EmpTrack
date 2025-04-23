package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;

public interface AdminService {

	Page<UserProxy> getAllUsersPageWise(PageRequest pageable);

	void deleteUser(String userName);

	String updateUser(UserProxy user);

	UserProxy getuser(String name);
}
