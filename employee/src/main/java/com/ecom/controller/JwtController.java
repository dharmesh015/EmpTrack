package com.ecom.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ecom.entity.JwtRequest;
import com.ecom.entity.JwtResponse;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.JwtService;
import com.ecom.service.TokenService;
import com.ecom.service.UserService;
//import com.ecom.service.impl.EmailserviceImpl;
//import jakarta.servlet.http.HttpSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin
public class JwtController {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService Service;

	@Autowired
	private TokenService tokenService;



	@PostMapping("/authenticate")
	public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest, HttpSession session) throws Exception {
		System.err.println(loginRequest.getCaptcha());
        String enteredCaptcha = loginRequest.getCaptcha();
        String storedCaptcha = (String) session.getAttribute("captcha");
        System.err.println("storedCaptcha--"+storedCaptcha);
     
        if (enteredCaptcha == null || !enteredCaptcha.equalsIgnoreCase(storedCaptcha)) {
            return ResponseEntity.badRequest().body("InvalidCAPTCHA");
        }

        try {
			if (loginRequest.getUserName() == null || loginRequest.getUserPassword() == null) {
				System.out.println("null data");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 JwtResponse jwtToken = jwtService.createJwtToken(loginRequest);
		 
		 return new ResponseEntity<>(jwtToken,HttpStatus.ACCEPTED);


    }

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping({ "/getdata/{token}" })
	public UserProxy getdata(@PathVariable("token") String token) {
		return jwtService.getdata(token);

	}
	

	@PostMapping("/registerNewUser")
	public ResponseEntity<?> registerNewUser(@RequestBody UserProxy user) {
		System.out.println("controler"+user.getPassword());
		 return new ResponseEntity<>(Service.registerNewUser(user),HttpStatus.ACCEPTED);
//		return Service.registerNewUser(user);
	}

	@GetMapping("/validate-token/{token}")
	public ResponseEntity<?> validateToken(@PathVariable(value = "token") String token) {
		System.err.println("token--" + token);
		String email = tokenService.validateToken(token);
		System.err.println(email);
		if (email != null) {
			Map<String, String> response = new HashMap<>();
			response.put("email", email);
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body("Invalid or expired token");
		}
	}
	
	@Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/registerWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>  registerWithImage(
            @RequestParam("userData") String userData,
            @RequestParam(value = "file", required = false) MultipartFile file) throws java.io.IOException {
        
        try {
            // Parse user data from JSON string
            UserProxy userProxy = objectMapper.readValue(userData, UserProxy.class);
            
            // Process and save image if provided
            if (file != null && !file.isEmpty()) {
                String imageUuid = saveProfileImage(file);
                userProxy.setImageUuid(imageUuid);
            }
            
            // Register the user

            return new ResponseEntity<>(Service.registerNewUser(userProxy),HttpStatus.ACCEPTED);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid or expired token");
            
        }
    }
    
    
    @GetMapping("/user-image/{imageUuid}")
    public ResponseEntity<Resource> getUserImage(@PathVariable String imageUuid) throws java.io.IOException {
        try {
            String imagePath = new ClassPathResource("").getFile().getAbsolutePath() 
                    + File.separator + "static" 
                    + File.separator + "documents" 
                    + File.separator + imageUuid;
            
            Resource resource = new UrlResource(Paths.get(imagePath).toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Adjust based on your image types
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageUuid + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String saveProfileImage(MultipartFile file) throws IOException, java.io.IOException {
        // Create a UUID for the file
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        
        // Get file extension
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Create the filename with UUID
        String newFilename = uuid + extension;
        
        // Get the path to save the file
        String uploadDir = new ClassPathResource("").getFile().getAbsolutePath() 
                + File.separator + "static" 
                + File.separator + "documents";
        
        // Create directories if they don't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Save the file
        String filePath = uploadDir + File.separator + newFilename;
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        
        return newFilename;
    }

}
