package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ecom.entity.Role;
import com.ecom.proxy.UserProxy;
public interface AdminService {
    Page<?> getAllUsersPageWise(PageRequest pageable);
    
    Page<?> getUsersByRole(String role, PageRequest pageable);
    
    Page<?> searchUsersByRole(String role, String query, PageRequest pageable);
    
    void deleteUser(String userName);
    
    String updateUser(UserProxy user);
    
    UserProxy getuser(String name);
    
    Page<?> searchUsers(String query, PageRequest pageable);
    
    String generateFakeUsers();
    
    List<Role> getAllRoles();
    
    boolean checkUsernameExists(String userName);
    
    boolean checkEmailExists(String email);
    
    // New method to reactivate a user
    String reactivateUser(String userName);

	Page<?> searchUsersByRole(String role, String query, PageRequest pageable, boolean activeOnly);
}