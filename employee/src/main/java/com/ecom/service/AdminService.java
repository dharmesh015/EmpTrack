package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ecom.entity.Role;
import com.ecom.proxy.UserProxy;
public interface AdminService {
    
    Page<?> getUsersByRole(String role, PageRequest pageable);
    
    Page<?> searchUsersByRole(String role, String query, PageRequest pageable);
    
    void deleteUser(String userName);
    
    String updateUser(UserProxy user);
    
    UserProxy getuser(String name);
    
    String generateFakeUsers();
    
}