//package com.ecom.service.impl;
//
//import org.apache.catalina.mapper.Mapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import com.ecom.configuration.enums.Gender;
//import com.ecom.dao.RoleDao;
//import com.ecom.dao.UserDao;
//import com.ecom.entity.Role;
//import com.ecom.entity.User;
//import com.ecom.proxy.UserProxy;
//import com.ecom.service.EmailService;
//import com.ecom.service.UserService;
//import com.ecom.util.MapperUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.MediaType;
//import org.springframework.http.HttpHeaders;
//import jakarta.annotation.PostConstruct;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//	@Autowired
//	private UserDao userDao;
//
//	@Autowired
//	private RoleDao roleDao;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//
//	@Autowired
//	private MapperUtil mapper;
//
//	@Autowired
//	private EmailService emailservice;
//
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	public String getEncodedPassword(String password) {
//		return passwordEncoder.encode(password);
//	}
//
//	@Override
//	public void updateUserRole(String userName, String roleobj) {
//		User byUserName = userDao.findByUserName(userName).get();
//		Role role = roleDao.findById(roleobj).orElseThrow(() -> new RuntimeException("Role not found"));
//		Set<Role> userRoles = new HashSet<>();
//		userRoles.add(role);
//		byUserName.setRole(userRoles);
//
//		userDao.save(byUserName);
//
//	}
//
//	@Override
//	public String registerNewUser(UserProxy usero) {
//		System.out.println("username--" + usero.getUserName());
//		if (userDao.findByUserName(usero.getUserName()).isPresent()) {
//			System.err.println("User NameExist");
//			return "UserNameExist";
//		}
//		if (userDao.findByEmail(usero.getEmail()).isPresent()) {
//			System.err.println("EmailExist");
//			return "EmailExist";
//		}
//		usero.setId(null);
//		User user = mapper.convertValue(usero, User.class);
//		try {
//			// Convert using the exact case that matches your enum
//			Gender gender = Gender.valueOf(usero.getGender().toUpperCase());
//			System.out.println("Converted gender: " + gender);
//			user.setGender(gender);
//		} catch (IllegalArgumentException e) {
//			System.err.println("Invalid gender value: " + usero.getGender());
//			return "InvalidGender";
//		}
//
//		// Set the image UUID if available
//		if (usero.getImageUuid() != null && !usero.getImageUuid().isEmpty()) {
//			user.setImageUuid(usero.getImageUuid());
//		}
//
//		user.setPassword(getEncodedPassword(user.getPassword()));
//		User savedUser = userDao.save(user);
//		Role role = roleDao.findById("USER").orElseThrow(() -> new RuntimeException("Role not found"));
//		Set<Role> userRoles = new HashSet<>();
//		userRoles.add(role);
//		savedUser.setRole(userRoles);
//		User userobj = userDao.save(savedUser);
//		return "register";
//	}
//
//	@Override
//	public String saveProfileImage(String userData, MultipartFile file) {
//
//		try {
//
//			UserProxy userProxy = objectMapper.readValue(userData, UserProxy.class);
//
//			if (file != null && !file.isEmpty()) {
//				
//				String uuid = UUID.randomUUID().toString();
//				String originalFilename = file.getOriginalFilename();
//
//				// Get file extension
//				String extension = "";
//				if (originalFilename != null && originalFilename.contains(".")) {
//					extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//				}
//
//				// Create the filename with UUID
//				String newFilename = uuid + extension;
//
//				// Get the path to save the file
//				String uploadDir = null;
//				try {
//					uploadDir = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
//							+ File.separator + "documents";
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				// Create directories if they don't exist
//				File directory = new File(uploadDir);
//				if (!directory.exists()) {
//					directory.mkdirs();
//				}
//
//				// Save the file
//				String filePath = uploadDir + File.separator + newFilename;
//				try {
//					Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				userProxy.setImageUuid(newFilename);
//			}
//
//			return registerNewUser(userProxy);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return "Invalid or expired token";
//
//		}
//
//		
//		
//	}
//
//	@Override
//	public ResponseEntity<Resource> getimage(String imageUuid) {
//		try {
//			String imagePath = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
//					+ File.separator + "documents" + File.separator + imageUuid;
//
//			Resource resource = new UrlResource(Paths.get(imagePath).toUri());
//
//			if (resource.exists()) {
//				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
//						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageUuid + "\"")
//						.body(resource);
//			} else {
//				return ResponseEntity.notFound().build();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.badRequest().build();
//		}
//	}
//}


package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ecom.configuration.enums.Gender;
import com.ecom.dao.RoleDao;
import com.ecom.dao.UserDao;
import com.ecom.entity.ApiResponse;
import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.service.EmailService;
import com.ecom.service.UserService;
import com.ecom.util.MapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private EmailService emailservice;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private Validator validator;

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void updateUserRole(String userName, String roleId) {
        User user = userDao.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleDao.findById(Long.parseLong(roleId))
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRole(userRoles);
        user.setModifiedAt(LocalDateTime.now());

        userDao.save(user);
    }

    @Override
    public ApiResponse registerNewUser(UserProxy userProxy) {
        // Validate the user proxy
        Set<ConstraintViolation<UserProxy>> violations = validator.validate(userProxy);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));
            
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessage);
        }
        
        
        Role role = roleDao.findById(2L)
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        // Check if username already exists
        if (userDao.findByUserName(userProxy.getUserName()).isPresent()) {
            return new ApiResponse(HttpStatus.CONFLICT.value(), "UserNameExist", 
                    "Username already exists. Please choose a different username.");
        }
        
        // Check if email already exists
        if (userDao.findByEmail(userProxy.getEmail()).isPresent()) {
            return new ApiResponse(HttpStatus.CONFLICT.value(), "EmailExist", 
                    "Email already exists. Please use a different email address.");
        }
        
        userProxy.setId(null);
        userProxy.setNrole(role);
        User user = mapper.convertValue(userProxy, User.class);
        
        try {
            // Convert gender to enum
            Gender gender = Gender.valueOf(userProxy.getGender().toUpperCase());
            user.setGender(gender);
            
            
        } catch (IllegalArgumentException e) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "InvalidGender", 
                    "Invalid gender value: " + userProxy.getGender());
        }

        // Set the image UUID if available
        if (userProxy.getImageUuid() != null && !userProxy.getImageUuid().isEmpty()) {
            user.setImageUuid(userProxy.getImageUuid());
        }

        user.setPassword(getEncodedPassword(user.getPassword()));
        
        // Set created and modified dates
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setModifiedAt(now);
       
        User savedUser = userDao.save(user);
        

        
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        savedUser.setRole(userRoles);
        savedUser.setNrole(role);
        
        userDao.save(savedUser);
        
        return new ApiResponse(HttpStatus.CREATED.value(), "register", 
                "User registered successfully",userProxy);
    }

    @Override
    public ApiResponse saveProfileImage(String userData, MultipartFile file) {
        try {
            UserProxy userProxy = objectMapper.readValue(userData, UserProxy.class);

            if (file != null && !file.isEmpty()) {
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
                String uploadDir = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
                        + File.separator + "documents";

                // Create directories if they don't exist
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Save the file
                String filePath = uploadDir + File.separator + newFilename;
                Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                
                userProxy.setImageUuid(newFilename);
            }

            return registerNewUser(userProxy);

        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Error", 
                    "Error processing image or user data: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Resource> getImage(String imageUuid) {
        try {
            String imagePath = new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
                    + File.separator + "documents" + File.separator + imageUuid;

            Resource resource = new UrlResource(Paths.get(imagePath).toUri());

            if (resource.exists()) {
                // Determine content type based on file extension
                String contentType = determineContentType(imageUuid);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageUuid + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
    

    @Override
    public ByteArrayOutputStream exportToExcel() {
        List<User> users = userDao.findAll();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("users_data");

        // Header Row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("Full Name");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Gender");
        headerRow.createCell(5).setCellValue("Date of Birth");
        headerRow.createCell(6).setCellValue("Contact Number");
        headerRow.createCell(7).setCellValue("Address");
        headerRow.createCell(8).setCellValue("Pin Code");
        headerRow.createCell(9).setCellValue("Roles");
        headerRow.createCell(10).setCellValue("Primary Role");
        headerRow.createCell(11).setCellValue("Created At");
        headerRow.createCell(12).setCellValue("Modified At");

        // Data Rows
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUserName());
            row.createCell(2).setCellValue(user.getName());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getGender().toString());  // Enum to string
            row.createCell(5).setCellValue(user.getDob().toString());  // LocalDate to String
            row.createCell(6).setCellValue(user.getContactNumber());
            row.createCell(7).setCellValue(user.getAddress());
            row.createCell(8).setCellValue(user.getPinCode());

            // Combine all roles into a single string
            String roles = user.getRole().stream()
                .map(role -> role.getRoleName())
                .collect(java.util.stream.Collectors.joining(", "));
            row.createCell(9).setCellValue(roles);

            // Display the primary role (nrole)
            if (user.getNrole() != null) {
                row.createCell(10).setCellValue(user.getNrole().getRoleName());
            } else {
                row.createCell(10).setCellValue("No primary role");
            }

            // Display created and modified dates
            if (user.getCreatedAt() != null) {
                row.createCell(11).setCellValue(user.getCreatedAt().toString());  // LocalDateTime to String
            } else {
                row.createCell(11).setCellValue("N/A");
            }

            if (user.getModifiedAt() != null) {
                row.createCell(12).setCellValue(user.getModifiedAt().toString());  // LocalDateTime to String
            } else {
                row.createCell(12).setCellValue("N/A");
            }
        }

        // Auto-size columns for better readability
        for (int i = 0; i < 13; i++) {  // Update column count if necessary
            sheet.autoSizeColumn(i);
        }

        // Write the workbook to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;
    }

}