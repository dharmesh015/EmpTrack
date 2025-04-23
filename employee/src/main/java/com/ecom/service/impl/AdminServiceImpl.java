package com.ecom.service.impl;

import java.util.List;
import java.util.Optional;
import com.ecom.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
//    	System.err.println(userDao.findAll(pageable));
		return userDao.findAll(pageable);

	}

	@Transactional
	public void deleteUser(String userName) {
	    User user = userDao.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
	    
	    // Delete payment details associated with the user's orders
	    
	    
	    // Check if the user has the "Seller" role
	    Optional<Role> sellerRole = roleDao.findByRoleName("Seller");
	    if (sellerRole.isPresent() && user.getRole().contains(sellerRole.get())) {
	        System.err.println("User " + userName + " is a seller. Deleting their products.");
	        
	        
	    }
	    
	    // Clear user roles and delete the user
	    user.getRole().clear();
	    userDao.delete(user);
	    
	    System.err.println("User " + userName + " successfully deleted");
	}
	
	public String updateUser(UserProxy user) {
		System.out.println(user.getGender());
		User userobj = userDao.findByUserName(user.getUserName()).orElseThrow(() -> new RuntimeException("User  not found"));
		  User newuser = mapperUtil.convertValue(user, User.class);

		userDao.save(newuser);
		return "saved";
	}

	public UserProxy getuser(String name) {
		Optional<User> userdata = userDao.findByUserName(name);
		return mapperUtil.convertValue(userdata.get(), UserProxy.class);
	}
}
