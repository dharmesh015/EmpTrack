
package com.ecom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ecom.proxy.UserProxy;

public interface AdminService {
    
    Page<?> getAllUsersPageWise(PageRequest pageable);
    
    void deleteUser(String userName);
    
    String updateUser(UserProxy user);
    
    UserProxy getuser(String name);
    
    Page<?> searchUsers(String query, PageRequest pageable);
    
    String generateFakeUsers();
}
