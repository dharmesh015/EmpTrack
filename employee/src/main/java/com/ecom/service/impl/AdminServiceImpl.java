package com.ecom.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.AdminService;
import com.ecom.util.MapperUtil;
import com.github.javafaker.Faker;

@Service
public class AdminServiceImpl implements AdminService {
    
    private final MapperUtil mapperUtil;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RoleDao roleDao;
    
    public AdminServiceImpl(MapperUtil mapperUtil) {
        this.mapperUtil = mapperUtil;
    }
    
    @Override
    public Page<?> getAllUsersPageWise(PageRequest pageable) {
        // Find users with role USER using the pageable which now includes sorting
        Page<User> allByRoleName = userDao.findByRole_RoleName("USER", pageable);
        
        // Convert to UserProxy and return the page
        return new PageImpl<>(
            mapperUtil.convertList(allByRoleName.getContent(), UserProxy.class), 
            pageable,
            allByRoleName.getTotalElements()
        );
    }
    
    @Override
    public Page<?> getUsersByRole(String role, PageRequest pageable) {
        // Find users with specified role using the pageable
        Page<User> usersByRole = userDao.findByRole_RoleName(role, pageable);
        
        // Convert to UserProxy and return the page
        return new PageImpl<>(
            mapperUtil.convertList(usersByRole.getContent(), UserProxy.class), 
            pageable,
            usersByRole.getTotalElements()
        );
    }
    
    @Override
    public Page<?> searchUsersByRole(String role, String query, PageRequest pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getUsersByRole(role, pageable);
        }
        
        // This assumes you have a searchUsersByRole method in your UserDao
        // If not, you'll need to add it (see implementation below)
        Page<User> searchResults = userDao.searchUsersByRole(role, query, pageable);
        
        return new PageImpl<>(
            mapperUtil.convertList(searchResults.getContent(), UserProxy.class),
            pageable,
            searchResults.getTotalElements()
        );
    }
    
    @Override
    @Transactional
    public void deleteUser(String userName) {
        User user = userDao.findByUserName(userName)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.getRole().clear();
        userDao.delete(user);
    }
    
    @Override
    public String updateUser(UserProxy user) {
        User existingUser = userDao.findByUserName(user.getUserName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Update user properties
        User updatedUser = mapperUtil.convertValue(user, User.class);
        
        // Preserve existing relationships and properties that shouldn't be updated
        updatedUser.setId(existingUser.getId());
        updatedUser.setRole(existingUser.getRole());
        updatedUser.setCreatedAt(existingUser.getCreatedAt());
        
        // If password is empty, keep the existing one
        if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
            updatedUser.setPassword(existingUser.getPassword());
        }
        
        userDao.save(updatedUser);
        return "saved";
    }
    
    @Override
    public UserProxy getuser(String name) {
        Optional<User> userdata = userDao.findByUserName(name);
        if (userdata.isPresent()) {
            return mapperUtil.convertValue(userdata.get(), UserProxy.class);
        }
        return null;
    }
    
    @Override
    public Page<?> searchUsers(String query, PageRequest pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllUsersPageWise(pageable);
        }
        
        Page<User> searchResults = userDao.searchUsers(query, pageable);
        
        return new PageImpl<>(
            mapperUtil.convertList(searchResults.getContent(), UserProxy.class),
            pageable,
            searchResults.getTotalElements()
        );
    }
    
    @Override
    public String generateFakeUsers() {
        Faker faker = new Faker();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        int count = 0;

        try {
            for (int i = 1; i <= 105; i++) {
                // Create new user
                User user = new User();
                user.setName(faker.name().fullName());
                user.setDob(faker.date().birthday().toInstant().atOffset(ZoneOffset.UTC).toLocalDate());
                user.setEmail(faker.internet().emailAddress());
                user.setUserName(faker.name().username() + i); // Ensure uniqueness
                user.setPassword(encoder.encode("Password@123")); // Default encrypted password
                user.setGender(faker.options().option(Gender.class));
                user.setAddress(faker.address().fullAddress());
                user.setImageUuid(null); // Optionally set a UUID for profile image
                user.setContactNumber(faker.phoneNumber().subscriberNumber(10));
                user.setPinCode(faker.address().zipCode());
                user.setCreatedAt(LocalDateTime.now());
                user.setModifiedAt(LocalDateTime.now());

                // Assign role to user (assuming you are assigning a role with ID 2, as per your previous logic)
                Role role = roleDao.findById(2L).orElseThrow(() -> new RuntimeException("Role not found"));
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(role);
                user.setRole(userRoles); // Set roles
                
                // Optionally, set the 'nrole' association if needed (assuming 'nrole' is the primary role)
                user.setNrole(role);

                // Save the user
                User savedUser = userDao.save(user);
                count++;
            }
            return count + " fake users added successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error generating fake users: " + e.getMessage());
        }
    }
    
    @Override
    public List<Role> getAllRoles() {
        return (List<Role>) roleDao.findAll();
    }
    
    @Override
    public boolean checkUsernameExists(String userName) {
        return userDao.findByUserName(userName).isPresent();
    }
    
    @Override
    public boolean checkEmailExists(String email) {
        return userDao.findByEmail(email).isPresent();
    }
    
    
}