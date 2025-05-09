
package com.ecom.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.entity.ApiResponse;
import com.ecom.entity.JwtRequest;
import com.ecom.entity.JwtResponse;
import com.ecom.proxy.UserProxy;
import com.ecom.service.JwtService;
import com.ecom.service.TokenService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
public class JwtController {

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenService tokenService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest, HttpSession session) throws Exception {
        String enteredCaptcha = loginRequest.getCaptcha();
        String storedCaptcha = (String) session.getAttribute("captcha");
        System.err.println(storedCaptcha);
        
        
        try {
            if (loginRequest.getUserName() == null || loginRequest.getUserPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "InvalidCredentials", "Username and password are required"));
            }
            
            JwtResponse jwtToken = jwtService.createJwtToken(loginRequest);
            return ResponseEntity.ok(jwtToken);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "AuthenticationFailed", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({ "/getdata/{token}" })
    public ResponseEntity<?> getdata(@PathVariable("token") String token) {
        try {
            UserProxy userProxy = jwtService.getdata(token);
            if (userProxy != null) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Success", "User data retrieved successfully", userProxy));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "UserNotFound", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", e.getMessage()));
        }
    }

    @PostMapping("/registerNewUser")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody UserProxy user) {
        ApiResponse response = userService.registerNewUser(user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<?> validateToken(@PathVariable(value = "token") String token) {
        String email = tokenService.validateToken(token);
        if (email != null) {
            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Valid", "Token is valid", response));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Invalid", "Invalid or expired token"));
        }
    }

    @PostMapping(value = "/registerWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerWithImage(
            @RequestParam("userData") String userData,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        try {
            ApiResponse response = userService.saveProfileImage(userData, file);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", 
                            "Error processing registration: " + e.getMessage()));
        }
    }

    @GetMapping("/user-image/{imageUuid}")
    public ResponseEntity<Resource> getUserImage(@PathVariable String imageUuid) {
        try {
            return userService.getImage(imageUuid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", e.getMessage()));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailExists(@PathVariable("email") String email) {
        try {
            boolean exists = userService.emailExists(email);
            if (exists) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(HttpStatus.OK.value(), "Exists", "Email already exists"));
            } else {
                return ResponseEntity.ok(
                    new ApiResponse(HttpStatus.OK.value(), "NotAvailable", "Email is Not available"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", "Something went wrong: " + e.getMessage()));
        }
    }

}