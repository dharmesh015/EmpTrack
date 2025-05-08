package com.ecom.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ecom.dao.UserDao;
import com.ecom.entity.User;
import com.ecom.service.TokenService;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private EmailserviceImpl emailService;

    private User testUser;
    private String testEmail;
    private String testPassword;
    private String testToken;
    private String resetLink;

    @BeforeEach
    public void setUp() {
        // Set up test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("dharmesh");
        testUser.setEmail("dharmesh@example.com");
        testUser.setPassword("dharmesh123");

        testEmail = "dharmesh@example.com";
        testPassword = "newPassword123";
        testToken = "test-reset-token-12345";
        resetLink = "http://localhost:4200/reset-password?token=" + testToken;

        // Set the sender value using ReflectionTestUtils since it's injected via @Value
        ReflectionTestUtils.setField(emailService, "sender", "noreply@yourapplication.com");
    }

    

    @Test
    public void testSendPasswordResetEmail_Success() {
        System.out.println("\n========== TEST: testSendPasswordResetEmail_Success ==========");
        
        // Arrange
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(tokenService.generatePasswordResetToken(testUser.getEmail(), testUser.getPassword())).thenReturn(testToken);
        
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());
        
        // Act
        String result = emailService.sendPasswordResetEmail(testEmail);
        
        // Assert
        assertEquals("S", result);
        verify(userDao).findByEmail(testEmail);
        verify(tokenService).generatePasswordResetToken(testUser.getEmail(), testUser.getPassword());
        verify(mailSender).send(any(SimpleMailMessage.class));
        
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("noreply@yourapplication.com", capturedMessage.getFrom());
        assertEquals(testEmail, capturedMessage.getTo()[0]);
        assertEquals("Password Reset Request", capturedMessage.getSubject());
        assertTrue(capturedMessage.getText().contains(resetLink));
        assertTrue(capturedMessage.getText().contains(testUser.getName()));
        
        System.out.println("Email sent to: " + testEmail);
        System.out.println("Token generated: " + testToken);
        System.out.println("Reset link: " + resetLink);
        System.out.println("Result: " + result);
        System.out.println("✓ Password reset email sent successfully");
        System.out.println("====================================\n");
    }

    @Test
    public void testSendPasswordResetEmail_UserNotFound() {
        System.out.println("\n========== TEST: testSendPasswordResetEmail_UserNotFound ==========");
        
        // Arrange
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.empty());
        
        // Act
        String result = emailService.sendPasswordResetEmail(testEmail);
        
        // Assert
        assertEquals("UNF", result);
        verify(userDao).findByEmail(testEmail);
        verify(tokenService, never()).generatePasswordResetToken(anyString(), anyString());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        
        System.out.println("Email attempted: " + testEmail);
        System.out.println("Result: " + result);
        System.out.println("✓ User not found handled correctly");
        System.out.println("====================================\n");
    }

    @Test
    public void testSendPasswordResetEmail_Exception() {
        System.out.println("\n========== TEST: testSendPasswordResetEmail_Exception ==========");
        
        // Arrange
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(tokenService.generatePasswordResetToken(testUser.getEmail(), testUser.getPassword())).thenReturn(testToken);
        doThrow(new RuntimeException("Mail server connection failed")).when(mailSender).send(any(SimpleMailMessage.class));
        
        // Act
        String result = emailService.sendPasswordResetEmail(testEmail);
        
        // Assert
        assertTrue(result.startsWith("Error:"));
        verify(userDao).findByEmail(testEmail);
        verify(tokenService).generatePasswordResetToken(testUser.getEmail(), testUser.getPassword());
        verify(mailSender).send(any(SimpleMailMessage.class));
        
        System.out.println("Email attempted: " + testEmail);
        System.out.println("Result: " + result);
        System.out.println("✓ Exception handled correctly");
        System.out.println("====================================\n");
    }

    @Test
    public void testResetPassword_Success() {
        System.out.println("\n========== TEST: testResetPassword_Success ==========");
        
        // Arrange
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(testPassword)).thenReturn("newHashedPassword456");
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userDao.save(userCaptor.capture())).thenReturn(testUser);
        
        // Act
        boolean result = emailService.resetPassword(testEmail, testPassword);
        
        // Assert
        assertTrue(result);
        verify(userDao).findByEmail(testEmail);
        verify(passwordEncoder).encode(testPassword);
        verify(userDao).save(any(User.class));
        
        User savedUser = userCaptor.getValue();
        assertEquals("newHashedPassword456", savedUser.getPassword());
        
        System.out.println("Email: " + testEmail);
        System.out.println("New password encoded: " + savedUser.getPassword());
        System.out.println("Result: " + result);
        System.out.println("✓ Password reset successful");
        System.out.println("====================================\n");
    }

    @Test
    public void testResetPassword_UserNotFound() {
        System.out.println("\n========== TEST: testResetPassword_UserNotFound ==========");
        
        // Arrange
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.empty());
        
        // Act
        boolean result = emailService.resetPassword(testEmail, testPassword);
        
        // Assert
        assertFalse(result);
        verify(userDao).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, never()).save(any(User.class));
        
        System.out.println("Email attempted: " + testEmail);
        System.out.println("Result: " + result);
        System.out.println("✓ User not found handled correctly");
        System.out.println("====================================\n");
    }

    

    @Test
    public void testGetEncodedPassword() {
        System.out.println("\n========== TEST: testGetEncodedPassword ==========");
        
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        
        // Act
        String result = emailService.getEncodedPassword(rawPassword);
        
        // Assert
        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + result);
        System.out.println("✓ Password encoding successful");
        System.out.println("====================================\n");
    }
}