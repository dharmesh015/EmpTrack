
package com.ecom.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.util.MapperUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.github.javafaker.Faker;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private Faker faker;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User testUser;
    private UserProxy testUserProxy;
    private PageRequest pageRequest;
    private List<User> userList;
    private List<UserProxy> userProxyList;
    private Page<User> userPage;

    @BeforeEach
    public void setUp() {
        Role testRole = new Role();
        testRole.setId(2L);
        testRole.setRoleName("USER");

        testUser = new User();
        testUser.setId(2L);
        testUser.setName("dharmesh");
        testUser.setUserName("dharmesh123");
        testUser.setEmail("dharmesh@example.com");
        testUser.setPassword("dharmesh123");
        testUser.setGender(Gender.MALE);
        testUser.setAddress("ahmedabd");
        testUser.setContactNumber("1234567890");
        testUser.setPinCode("123456");
        testUser.setDob(LocalDate.of(2000, 1, 1));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setModifiedAt(LocalDateTime.now());
        testUser.setActive(true);
        testUser.setRole(new HashSet<>(Set.of(testRole)));
        testUser.setNrole(testRole);

        testUserProxy = new UserProxy();
        testUserProxy.setName("dharmesh");
        testUserProxy.setUserName("dharmesh123");
        testUserProxy.setEmail("dharmesh@example.com");
        testUserProxy.setGender("MALE");
        testUserProxy.setAddress("ahmedabd");
        testUserProxy.setContactNumber("1234567890");
        testUserProxy.setPinCode("123456");
        testUserProxy.setDob(LocalDate.of(2000, 1, 1));

        pageRequest = PageRequest.of(0, 10);
        userList = List.of(testUser);
        userProxyList = List.of(testUserProxy);
        userPage = new PageImpl<>(userList, pageRequest, userList.size());
    }

    @Test
    public void testGetUsersByRole() {
        System.out.println("\n========== TEST: testGetUsersByRole ==========");
        
        // Arrange
        when(userDao.findByRole_RoleNameAndActiveTrue(eq("USER"), any(Pageable.class))).thenReturn(userPage);
        when(mapperUtil.convertList(eq(userList), eq(UserProxy.class))).thenReturn(userProxyList);

        // Act
        Page<?> result = adminService.getUsersByRole("USER", pageRequest);

        // Print data in a structured format
        System.out.println("\n----- Data Retrieved from Database (User Entity) -----");
        userList.forEach(user -> {
            System.out.println("User ID: " + user.getId());
            System.out.println("User Name: " + user.getName());
            System.out.println("User Email: " + user.getEmail());
            System.out.println("User Role: " + user.getRole());
            System.out.println("User Active: " + user.isActive());
            System.out.println("User DOB: " + user.getDob());
            System.out.println("---------------------");
        });

        // Data after mapping
        System.out.println("\n----- Mapped Data to UserProxy -----");
        userProxyList.forEach(userProxy -> {
            System.out.println("UserProxy Name: " + userProxy.getName());
            System.out.println("UserProxy Username: " + userProxy.getUserName());
            System.out.println("UserProxy Email: " + userProxy.getEmail());
            System.out.println("UserProxy Gender: " + userProxy.getGender());
            System.out.println("UserProxy Address: " + userProxy.getAddress());
            System.out.println("UserProxy Contact: " + userProxy.getContactNumber());
            System.out.println("UserProxy Pin Code: " + userProxy.getPinCode());
            System.out.println("UserProxy DOB: " + userProxy.getDob());
            System.out.println("---------------------");
        });

        // Data in the result
        System.out.println("\n----- Data Retrieved in the Result (Page Content) -----");
        result.getContent().forEach(item -> {
            if (item instanceof UserProxy) {
                UserProxy userProxy = (UserProxy) item;
                System.out.println("UserProxy Name: " + userProxy.getName());
                System.out.println("UserProxy Username: " + userProxy.getUserName());
                System.out.println("UserProxy Email: " + userProxy.getEmail());
                System.out.println("UserProxy Gender: " + userProxy.getGender());
                System.out.println("UserProxy Address: " + userProxy.getAddress());
                System.out.println("UserProxy Contact: " + userProxy.getContactNumber());
                System.out.println("UserProxy Pin Code: " + userProxy.getPinCode());
                System.out.println("UserProxy DOB: " + userProxy.getDob());
                System.out.println("---------------------");
            }
        });

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserProxy, result.getContent().get(0));
        verify(userDao).findByRole_RoleNameAndActiveTrue("USER", pageRequest);
        verify(mapperUtil).convertList(userList, UserProxy.class);
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ All assertions passed");
        System.out.println("Total elements: " + result.getTotalElements());
        System.out.println("====================================\n");
    }

    @Test
    public void testSearchUsersByRole_WithValidQuery() {
        System.out.println("\n========== TEST: testSearchUsersByRole_WithValidQuery ==========");
        
        // Given
        String query = "dharmesh";
        when(userDao.searchUsersByRoleAndActiveTrue(anyString(), anyString(), any(PageRequest.class))).thenReturn(userPage);
        when(mapperUtil.convertList(anyList(), eq(UserProxy.class))).thenReturn(userProxyList);
        
        // When
        System.out.println("Executing search with query: \"" + query + "\"");
        Page<?> result = adminService.searchUsersByRole("USER", query, pageRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userDao).searchUsersByRoleAndActiveTrue("USER", query, pageRequest);
        verify(mapperUtil).convertList(userList, UserProxy.class);
        
        System.out.println("\n----- Search Results -----");
        System.out.println("Total elements: " + result.getTotalElements());
        System.out.println("Page size: " + result.getSize());
        System.out.println("Total pages: " + result.getTotalPages());
        System.out.println("✓ All assertions passed");
        System.out.println("====================================\n");
    }
    
    @Test
    public void testSearchUsersByRole_emptyQuery() {
        System.out.println("\n========== TEST: testSearchUsersByRole_emptyQuery ==========");
        
        // Arrange
        when(userDao.findByRole_RoleNameAndActiveTrue(eq("USER"), any(Pageable.class))).thenReturn(userPage);
        when(mapperUtil.convertList(eq(userList), eq(UserProxy.class))).thenReturn(userProxyList);

        // Act
        System.out.println("Executing search with empty query");
        Page<?> result = adminService.searchUsersByRole("USER", "", pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserProxy, result.getContent().get(0));
        verify(userDao).findByRole_RoleNameAndActiveTrue("USER", pageRequest);
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ Empty query correctly used findByRole method instead of search");
        System.out.println("✓ All assertions passed");
        System.out.println("====================================\n");
    }

    @Test
    public void testSearchUsersByRole_nullQuery() {
        System.out.println("\n========== TEST: testSearchUsersByRole_nullQuery ==========");
        
        // Arrange
        when(userDao.findByRole_RoleNameAndActiveTrue(eq("USER"), any(Pageable.class)))
            .thenReturn(userPage);
        when(mapperUtil.convertList(eq(userList), eq(UserProxy.class)))
            .thenReturn(userProxyList);

        // Act
        System.out.println("Executing search with null query");
        Page<?> result = adminService.searchUsersByRole("USER", null, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserProxy, result.getContent().get(0));
        verify(userDao).findByRole_RoleNameAndActiveTrue("USER", pageRequest);
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ Null query correctly used findByRole method instead of search");
        System.out.println("✓ All assertions passed");
        System.out.println("====================================\n");
    }

    @Test
    public void testDeleteUser() {
        System.out.println("\n========== TEST: testDeleteUser ==========");
        
        // Arrange
        when(userDao.findByUserName("testuser")).thenReturn(Optional.of(testUser));

        // Act
        System.out.println("----- User Before Deletion -----");
        System.out.println("Username: " + testUser.getUserName());
        System.out.println("Active Status: " + testUser.isActive());
        
        adminService.deleteUser("testuser");
        
        System.out.println("\n----- User After Deletion -----");
        System.out.println("Username: " + testUser.getUserName());
        System.out.println("Active Status: " + testUser.isActive());

        // Assert
        assertFalse(testUser.isActive());  // Assert that the user's active status is false
        verify(userDao).save(testUser);  // Ensure save method was called to persist the change
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ User was successfully marked as inactive");
        System.out.println("✓ All assertions passed");
        System.out.println("====================================\n");
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        System.out.println("\n========== TEST: testDeleteUser_UserNotFound ==========");
        
        // Arrange
        when(userDao.findByUserName("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Attempting to delete non-existent user 'testuser'");
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminService.deleteUser("testuser");
        });
        
        // Check message
        assertEquals("User not found", thrown.getMessage());
        
        // Ensure save was not called
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ Exception correctly thrown with message: " + thrown.getMessage());
        System.out.println("✓ Save method was correctly not called");
        System.out.println("====================================\n");
    }

    @Test
    public void testUpdateUser() {
        System.out.println("\n========== TEST: testUpdateUser ==========");
        
        // Arrange
        UserProxy updateUserProxy = new UserProxy();
        updateUserProxy.setUserName("testuser");
        updateUserProxy.setEmail("newemail@example.com");
        
        User existingUser = new User();
        existingUser.setUserName("testuser");
        existingUser.setEmail("oldemail@example.com");
        
        // Mock the findByUserName method to return the existing user
        when(userDao.findByUserName("testuser")).thenReturn(Optional.of(existingUser));
        
        // Mock the converter to return a NEW user with the updated email
        User updatedUser = new User();
        updatedUser.setUserName("testuser");
        updatedUser.setEmail("newemail@example.com");
        when(mapperUtil.convertValue(eq(updateUserProxy), eq(User.class))).thenReturn(updatedUser);
        
        // Mock the save method to return the updated user
        when(userDao.save(any(User.class))).thenReturn(updatedUser);
        
        // Log test details
        System.out.println("----- User Update Information -----");
        System.out.println("Original Email: " + existingUser.getEmail());
        System.out.println("New Email: " + updateUserProxy.getEmail());
        
        // Act
        String result = adminService.updateUser(updateUserProxy);
        
        // Assert
        assertEquals("saved", result);
        verify(userDao).save(any(User.class));
        
        // Capture the argument passed to save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        // Assert the email was updated
        assertEquals("newemail@example.com", savedUser.getEmail());
        
        System.out.println("\n----- Update Results -----");
        System.out.println("Service Response: " + result);
        System.out.println("Saved User Email: " + savedUser.getEmail());
        System.out.println("✓ All assertions passed");
        System.out.println("====================================\n");
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        System.out.println("\n========== TEST: testUpdateUser_UserNotFound ==========");
        
        // Arrange
        UserProxy updateUserProxy = new UserProxy();
        updateUserProxy.setUserName("testuser");
        updateUserProxy.setEmail("newemail@example.com");
        
        // Mock the findByUserName method to return empty (user not found)
        when(userDao.findByUserName("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Attempting to update non-existent user 'testuser'");
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            adminService.updateUser(updateUserProxy);
        });
        
        // Assert the exception message
        assertEquals("User not found", thrown.getMessage());
        
        // Ensure save was not called
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("\n----- Test Results -----");
        System.out.println("✓ Exception correctly thrown with message: " + thrown.getMessage());
        System.out.println("✓ Save method was correctly not called");
        System.out.println("====================================\n");
    }

    
    
}