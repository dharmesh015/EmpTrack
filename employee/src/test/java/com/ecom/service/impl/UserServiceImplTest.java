package com.ecom.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;
import com.ecom.entity.ApiResponse;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.EmailService;
import com.ecom.util.MapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MapperUtil mapper;

    @Mock
    private EmailService emailService;

    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private Validator validator;
    
    @Mock
    private ClassPathResource classPathResource;
    
    @Mock
    private UrlResource urlResource;

    @InjectMocks
    private UserServiceImpl userService;
    
    private Role userRole;
    private User testUser;
    private UserProxy testUserProxy;
    private Set<ConstraintViolation<UserProxy>> emptyViolations;

    @BeforeEach
    public void setUp() {
        userRole = new Role();
        userRole.setId(2L);
        userRole.setRoleName("USER");

        testUser = new User();
        testUser.setId(2L);
        testUser.setName("Dharmesh");
        testUser.setUserName("Dharmesh123");
        testUser.setEmail("Dharmesh@example.com");
        testUser.setPassword("Dharmesh123");
        testUser.setGender(Gender.MALE);
        testUser.setAddress("ahmedabad");
        testUser.setContactNumber("1234567890");
        testUser.setPinCode("123456");
        testUser.setDob(LocalDate.of(2001, 1, 1));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setModifiedAt(LocalDateTime.now());
        testUser.setActive(true);
        testUser.setRole(new HashSet<>(Set.of(userRole)));
        testUser.setNrole(userRole);

        testUserProxy = new UserProxy();
        testUserProxy.setId(2L);
        testUserProxy.setName("Dharmesh");
        testUserProxy.setUserName("Dharmesh123");
        testUserProxy.setEmail("Dharmesh@example.com");
        testUserProxy.setPassword("Dharmesh123");
        testUserProxy.setGender("MALE");
        testUserProxy.setAddress("ahmedabad");
        testUserProxy.setContactNumber("1234567890");
        testUserProxy.setPinCode("123456");
        testUserProxy.setDob(LocalDate.of(2001, 1, 1));
        
        emptyViolations = new HashSet<>();
    }

    
    @Test
    public void testRegisterNewUser_Success() {
        System.out.println("\n========== TEST: testRegisterNewUser_Success ==========");
        
        // Arrange
        when(validator.validate(testUserProxy)).thenReturn(emptyViolations);
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));
        when(userDao.findByUserName(testUserProxy.getUserName())).thenReturn(Optional.empty());
        when(userDao.findByEmail(testUserProxy.getEmail())).thenReturn(Optional.empty());
        when(mapper.convertValue(testUserProxy, User.class)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");  
        when(userDao.save(any(User.class))).thenReturn(testUser);
        
        // Act
        ApiResponse response = userService.registerNewUser(testUserProxy);
        
        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("register", response.getCode());
        assertEquals("User registered successfully", response.getMessage());
        
        // Verify interactions
        verify(validator).validate(testUserProxy);
        verify(roleDao).findById(2L);
        verify(userDao).findByUserName(testUserProxy.getUserName());
        verify(userDao).findByEmail(testUserProxy.getEmail());
        verify(mapper).convertValue(testUserProxy, User.class);
        verify(userDao, times(2)).save(any(User.class));
        
        System.out.println("User proxy: " + testUserProxy.getUserName() + ", " + testUserProxy.getEmail());
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response description: " + response.getMessage());
        System.out.println("✓ User registered successfully");
        System.out.println("====================================\n");
    }
    
    @Test
    public void testRegisterNewUser_ValidationFailure() {
        System.out.println("\n========== TEST: testRegisterNewUser_ValidationFailure ==========");

        // Clone the testUserProxy object and set email to null
        UserProxy testUserProxyWithNullEmail = new UserProxy();
        BeanUtils.copyProperties(testUserProxy, testUserProxyWithNullEmail);
        testUserProxyWithNullEmail.setEmail(null);  // Set email to null

        // Arrange validation failure for null email
        Set<ConstraintViolation<UserProxy>> violations = new HashSet<>();
        ConstraintViolation<UserProxy> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Email cannot be null");
        violations.add(violation);

        // Mock the validator to return the violations
        when(validator.validate(testUserProxyWithNullEmail)).thenReturn(violations);

        // Act
        ApiResponse response = userService.registerNewUser(testUserProxyWithNullEmail);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Validation failed", response.getCode());
        assertEquals("Email cannot be null", response.getMessage());  // Expected validation message

        // Verify no further interaction with DAO methods
        verify(roleDao, never()).findById(anyLong());
        verify(userDao, never()).findByUserName(anyString());
        verify(userDao, never()).save(any(User.class));

        // Log the results for the test
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response description: " + response.getMessage());
        System.out.println("✓ Validation failure handled correctly");
        System.out.println("====================================\n");
    }

    
    @Test
    public void testRegisterNewUser_UsernameExists() {
        System.out.println("\n========== TEST: testRegisterNewUser_UsernameExists ==========");
        
        // Arrange
        when(validator.validate(testUserProxy)).thenReturn(emptyViolations);
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));
        when(userDao.findByUserName(testUserProxy.getUserName())).thenReturn(Optional.of(testUser));
        
        // Act
        ApiResponse response = userService.registerNewUser(testUserProxy);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("UserNameExist", response.getCode());
        assertTrue(response.getMessage().contains("Username already exists"));
        
        // Verify interactions
        verify(validator).validate(testUserProxy);
        verify(roleDao).findById(2L);
        verify(userDao).findByUserName(testUserProxy.getUserName());
        verify(userDao, never()).findByEmail(anyString());
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("Username tested: " + testUserProxy.getUserName());
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response description: " + response.getMessage());
        System.out.println("✓ Username existence check handled correctly");
        System.out.println("====================================\n");
    }

    @Test
    public void testRegisterNewUser_EmailExists() {
        System.out.println("\n========== TEST: testRegisterNewUser_EmailExists ==========");
        
        // Arrange
        when(validator.validate(testUserProxy)).thenReturn(emptyViolations);
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));
        when(userDao.findByUserName(testUserProxy.getUserName())).thenReturn(Optional.empty());
        when(userDao.findByEmail(testUserProxy.getEmail())).thenReturn(Optional.of(testUser));
        
        // Act
        ApiResponse response = userService.registerNewUser(testUserProxy);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("EmailExist", response.getCode());
        assertTrue(response.getMessage().contains("Email already exists"));
        
        // Verify interactions
        verify(validator).validate(testUserProxy);
        verify(roleDao).findById(2L);
        verify(userDao).findByUserName(testUserProxy.getUserName());
        verify(userDao).findByEmail(testUserProxy.getEmail());
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("Email tested: " + testUserProxy.getEmail());
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response description: " + response.getMessage());
        System.out.println("✓ Email existence check handled correctly");
        System.out.println("====================================\n");
    }

    @Test
    public void testRegisterNewUser_InvalidGender() {
        System.out.println("\n========== TEST: testRegisterNewUser_InvalidGender ==========");
        
        // Arrange
        testUserProxy.setGender("INVALID_GENDER");
        
        when(validator.validate(testUserProxy)).thenReturn(emptyViolations);
        when(roleDao.findById(2L)).thenReturn(Optional.of(userRole));
        when(userDao.findByUserName(testUserProxy.getUserName())).thenReturn(Optional.empty());
        when(userDao.findByEmail(testUserProxy.getEmail())).thenReturn(Optional.empty());
        when(mapper.convertValue(testUserProxy, User.class)).thenReturn(testUser);
        
        // Act
        ApiResponse response = userService.registerNewUser(testUserProxy);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("InvalidGender", response.getCode());
        assertTrue(response.getMessage().contains("Invalid gender value"));
        
        // Verify interactions
        verify(validator).validate(testUserProxy);
        verify(roleDao).findById(2L);
        verify(userDao).findByUserName(testUserProxy.getUserName());
        verify(userDao).findByEmail(testUserProxy.getEmail());
        verify(mapper).convertValue(testUserProxy, User.class);
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("Invalid gender value: " + testUserProxy.getGender());
        System.out.println("Response status: " + response.getStatus());
        System.out.println("Response message: " + response.getMessage());
        System.out.println("Response description: " + response.getMessage());
        System.out.println("✓ Invalid gender handled correctly");
        System.out.println("====================================\n");
    }

    

    

    @Test
    public void testExportToExcel() throws IOException {
        System.out.println("\n========== TEST: testExportToExcel ==========");
        
        // Arrange
        List<User> users = List.of(testUser);
        when(userDao.findAll()).thenReturn(users);
        
        // Act
        ByteArrayOutputStream result = userService.exportToExcel();
        
        // Load the created Excel file for validation
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        
        // Verify workbook structure
        assertEquals("users_data", sheet.getSheetName());
        assertEquals("ID", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("Username", sheet.getRow(0).getCell(1).getStringCellValue());
        
        // Verify user data
        Row dataRow = sheet.getRow(1);
        assertEquals(testUser.getId().doubleValue(), dataRow.getCell(0).getNumericCellValue());
        assertEquals(testUser.getUserName(), dataRow.getCell(1).getStringCellValue());
        assertEquals(testUser.getName(), dataRow.getCell(2).getStringCellValue());
        assertEquals(testUser.getEmail(), dataRow.getCell(3).getStringCellValue());
        
        workbook.close();
        
        System.out.println("Number of users exported: " + users.size());
        System.out.println("Excel bytes size: " + result.size());
        System.out.println("✓ Excel export successful");
        System.out.println("====================================\n");
    }
    
    



   
}