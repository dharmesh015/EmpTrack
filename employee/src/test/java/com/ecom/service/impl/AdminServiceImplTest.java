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
        // Arrange
        when(userDao.findByRole_RoleNameAndActiveTrue(eq("USER"), any(Pageable.class))).thenReturn(userPage);
        when(mapperUtil.convertList(eq(userList), eq(UserProxy.class))).thenReturn(userProxyList);

        // Act
        Page<?> result = adminService.getUsersByRole("USER", pageRequest);

        System.out.println("Data retrieved in the result (Page content):");
        result.getContent().forEach(item -> {
            if (item instanceof UserProxy) {
                UserProxy userProxy = (UserProxy) item;  // Cast the object to UserProxy
                System.out.println("UserProxy Name: " + userProxy.getName());
                System.out.println("UserProxy Username: " + userProxy.getUserName());
                System.out.println("UserProxy Email: " + userProxy.getEmail());
                System.out.println("UserProxy Gender: " + userProxy.getGender());
                System.out.println("UserProxy Address: " + userProxy.getAddress());
                System.out.println("UserProxy Contact: " + userProxy.getContactNumber());
                System.out.println("UserProxy Pin Code: " + userProxy.getPinCode());
                System.out.println("UserProxy DOB: " + userProxy.getDob());
                System.out.println("-------------");
            }
        });
        // Print the data retrieved from the database (User entity)
        System.out.println("Data retrieved from database (User entity):");
        userList.forEach(user -> {
            System.out.println("User ID: " + user.getId());
            System.out.println("User Name: " + user.getName());
            System.out.println("User Email: " + user.getEmail());
            System.out.println("User Role: " + user.getRole());
            System.out.println("User Active: " + user.isActive());
            System.out.println("User DOB: " + user.getDob());
            System.out.println("-------------");
        });

        // Print the data after conversion to UserProxy
        System.out.println("Mapped Data to UserProxy:");
        userProxyList.forEach(userProxy -> {
            System.out.println("UserProxy Name: " + userProxy.getName());
            System.out.println("UserProxy Username: " + userProxy.getUserName());
            System.out.println("UserProxy Email: " + userProxy.getEmail());
            System.out.println("UserProxy Gender: " + userProxy.getGender());
            System.out.println("UserProxy Address: " + userProxy.getAddress());
            System.out.println("UserProxy Contact: " + userProxy.getContactNumber());
            System.out.println("UserProxy Pin Code: " + userProxy.getPinCode());
            System.out.println("UserProxy DOB: " + userProxy.getDob());
            System.out.println("-------------");
        });
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserProxy, result.getContent().get(0));
        verify(userDao).findByRole_RoleNameAndActiveTrue("USER", pageRequest);
        verify(mapperUtil).convertList(userList, UserProxy.class);
        System.out.println("Test passed: " + result.getContent());
    }



    @Test
    public void testSearchUsersByRole_WithValidQuery() {
        // Given
        String query = "test";
        when(userDao.searchUsersByRoleAndActiveTrue(anyString(), anyString(), any(PageRequest.class))).thenReturn(userPage);
        when(mapperUtil.convertList(anyList(), eq(UserProxy.class))).thenReturn(userProxyList);
        
        // When
        Page<?> result = adminService.searchUsersByRole("USER", query, pageRequest);
        
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userDao).searchUsersByRoleAndActiveTrue("USER", query, pageRequest);
        verify(mapperUtil).convertList(userList, UserProxy.class);
    }
    
    
    @Test
    public void testSearchUsersByRole_emptyQuery() {
        // Arrange
        when(userDao.findByRole_RoleNameAndActiveTrue(eq("USER"), any(Pageable.class))).thenReturn(userPage);
        when(mapperUtil.convertList(eq(userList), eq(UserProxy.class))).thenReturn(userProxyList);

        // Act
        Page<?> result = adminService.searchUsersByRole("USER", "", pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserProxy, result.getContent().get(0));
        verify(userDao).findByRole_RoleNameAndActiveTrue("USER", pageRequest);  // Verifying the correct DAO method is called
    }

    
    @Test
    public void testDeleteUser() {
        // Arrange
        when(userDao.findByUserName("testuser")).thenReturn(Optional.of(testUser));

        // Act
        adminService.deleteUser("testuser");

        // Assert
        assertFalse(testUser.isActive());  // Assert that the user's active status is false
        verify(userDao).save(testUser);  // Ensure save method was called to persist the change
    }

    
    @Test
    public void testUpdateUser() {
        // Arrange
        UserProxy updateUserProxy = new UserProxy();
        updateUserProxy.setUserName("testuser");
        updateUserProxy.setEmail("newemail@example.com");

        when(userDao.findByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(mapperUtil.convertValue(eq(updateUserProxy), eq(User.class))).thenReturn(testUser);

        // Act
        String result = adminService.updateUser(updateUserProxy);

        // Assert
        assertEquals("saved", result);  // Ensure the correct message is returned
        assertEquals("newemail@example.com", testUser.getEmail());  // Ensure the user was updated correctly
        verify(userDao).save(testUser);  // Verify save was called
    }
    @Test
    public void testGenerateFakeUsers() {
        // Arrange
        when(roleDao.findById(2L)).thenReturn(Optional.of(new Role()));
        when(userDao.save(any(User.class))).thenReturn(testUser);

        // Act
        String result = adminService.generateFakeUsers();

        // Assert
        assertTrue(result.contains("fake users added successfully"));
        verify(userDao, times(105)).save(any(User.class));  // Verify that 105 users are saved
    }
}