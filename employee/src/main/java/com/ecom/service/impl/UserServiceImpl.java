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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import jakarta.validation.Validation;
import jakarta.validation.Validator;

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
        userProxy.setActive(true);
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
    
    @Override
    public ByteArrayOutputStream generateBlankExcelTemplate() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("users_data");

        
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");
        headerRow.createCell(1).setCellValue("Full Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Gender");
        headerRow.createCell(4).setCellValue("Date of Birth");
        headerRow.createCell(5).setCellValue("Contact Number");
        headerRow.createCell(6).setCellValue("Address");
        headerRow.createCell(7).setCellValue("Pin Code");


        
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    @Override
    public boolean emailExists(String email) {
        return userDao.findByEmail(email).isPresent();
    }
    
    @Override
    public List<String> importUsersFromFile(MultipartFile file) {
        List<User> users = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Role userRole = roleDao.findByRoleName("USER").get(); 
        if (userRole == null) throw new RuntimeException("USER role not found.");
        
        Set<String> existingUsernames = userDao.findAll().stream()
                .map(User::getUserName)
                .collect(Collectors.toSet());
                
        Set<String> existingEmails = userDao.findAll().stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
        
        Set<String> processingUsernames = new HashSet<>();
        Set<String> processingEmails = new HashSet<>();
        
        // Get validator to validate against proxy constraints
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        if (file.getOriginalFilename().endsWith(".csv")) {
            processCsvFile(file, users, errors, userRole, existingUsernames, existingEmails, 
                          processingUsernames, processingEmails, validator);
        } else {
            processExcelFile(file, users, errors, userRole, existingUsernames, existingEmails, 
                            processingUsernames, processingEmails, validator);
        }
        
        if (!users.isEmpty()) {
            userDao.saveAll(users);
        }
        
        return errors;
    }

    private void processCsvFile(MultipartFile file, List<User> users, List<String> errors, 
                               Role userRole, Set<String> existingUsernames, Set<String> existingEmails,
                               Set<String> processingUsernames, Set<String> processingEmails, Validator validator) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            reader.readLine(); // skip header
            String line;
            int rowNum = 1; // Header is row 0
            
            while ((line = reader.readLine()) != null) {
                rowNum++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 8) {
                        errors.add("Row " + rowNum + ": Insufficient data");
                        continue;
                    }
                    
                    // Create a UserProxy for validation
                    UserProxy userProxy = new UserProxy();
                    
                    // Username validation
                    String username = data[0].trim();
                    if (username.isEmpty()) {
                        errors.add("Row " + rowNum + ": Username is required");
                        continue;
                    }
                    userProxy.setUserName(username);
                    
                    // Validate username uniqueness
                    if (isUsernameTaken(username, existingUsernames, processingUsernames)) {
                        errors.add("Row " + rowNum + ": Username '" + username + "' already exists");
                        continue;
                    }
                    
                    // Name validation
                    String name = data[1].trim();
                    if (name.isEmpty()) {
                        errors.add("Row " + rowNum + ": Name is required");
                        continue;
                    }
                    userProxy.setName(name);
                    
                    // Email validation
                    String email = data[2].trim();
                    if (email.isEmpty()) {
                        errors.add("Row " + rowNum + ": Email is required");
                        continue;
                    }
                    userProxy.setEmail(email);
                    
                    // Validate email uniqueness
                    if (isEmailTaken(email, existingEmails, processingEmails)) {
                        errors.add("Row " + rowNum + ": Email '" + email + "' already exists");
                        continue;
                    }
                    
                    // Gender validation
                    String genderStr = data[3].trim();
                    if (genderStr.isEmpty()) {
                        errors.add("Row " + rowNum + ": Gender is required");
                        continue;
                    }
                    userProxy.setGender(genderStr);
                    
                    Gender gender;
                    try {
                        gender = Gender.valueOf(genderStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        errors.add("Row " + rowNum + ": Invalid gender value");
                        continue;
                    }
                    
                    // Date of birth validation
                    String dobStr = data[4].trim();
                    if (dobStr.isEmpty()) {
                        errors.add("Row " + rowNum + ": Date of birth is required");
                        continue;
                    }
                    
                    LocalDate dob;
                    try {
                        dob = LocalDate.parse(dobStr);
                        if (!dob.isBefore(LocalDate.now())) {
                            errors.add("Row " + rowNum + ": Date of birth must be in the past");
                            continue;
                        }
                        userProxy.setDob(dob);
                    } catch (Exception e) {
                        errors.add("Row " + rowNum + ": Invalid date format");
                        continue;
                    }
                    
                    // Contact number validation
                    String contactNumber = data[5].trim();
                    if (contactNumber.isEmpty()) {
                        errors.add("Row " + rowNum + ": Contact number is required");
                        continue;
                    }
                    if (!contactNumber.matches("^[0-9]{10}$")) {
                        errors.add("Row " + rowNum + ": Contact number must be 10 digits");
                        continue;
                    }
                    userProxy.setContactNumber(contactNumber);
                    
                    // Address validation
                    String address = data[6].trim();
                    if (address.isEmpty()) {
                        errors.add("Row " + rowNum + ": Address is required");
                        continue;
                    }
                    if (address.length() < 5) {
                        errors.add("Row " + rowNum + ": Address must be at least 5 characters long");
                        continue;
                    }
                    userProxy.setAddress(address);
                    
                    // Pin code validation
                    String pinCode = data[7].trim();
                    if (pinCode.isEmpty()) {
                        errors.add("Row " + rowNum + ": Pin code is required");
                        continue;
                    }
                    if (!pinCode.matches("^[0-9]{6}$")) {
                        errors.add("Row " + rowNum + ": Pin code must be 6 digits");
                        continue;
                    }
                    userProxy.setPinCode(pinCode);
                    
                    // Set password (default)
                    String defaultPassword = "Welcome@123";
                    userProxy.setPassword(defaultPassword);
                    
                    // Validate the proxy using bean validation
                    Set<ConstraintViolation<UserProxy>> violations = validator.validate(userProxy);
                    if (!violations.isEmpty()) {
                        List<String> validationErrors = violations.stream()
                            .map(violation -> violation.getMessage())
                            .collect(Collectors.toList());
                        errors.add("Row " + rowNum + ": " + String.join(", ", validationErrors));
                        continue;
                    }
                    
                    // If validation passed, create User entity
                    User user = new User();
                    user.setUserName(username);
                    user.setName(name);
                    user.setEmail(email);
                    user.setGender(gender);
                    user.setDob(dob);
                    user.setContactNumber(contactNumber);
                    user.setAddress(address);
                    user.setPinCode(pinCode);
                    
                    // Default values
                    user.setPassword(defaultPassword);
                    user.setRole(Set.of(userRole));
                    user.setNrole(userRole);
                    user.setActive(true);
                    
                    users.add(user);
                    processingUsernames.add(username);
                    processingEmails.add(email);
                    
                } catch (Exception e) {
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("Error reading CSV file: " + e.getMessage());
        }
    }

    private void processExcelFile(MultipartFile file, List<User> users, List<String> errors, 
                                 Role userRole, Set<String> existingUsernames, Set<String> existingEmails,
                                 Set<String> processingUsernames, Set<String> processingEmails, Validator validator) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    // Create a UserProxy for validation
                    UserProxy userProxy = new UserProxy();
                    
                    // Username validation
                    String username = getCellValueAsString(row.getCell(0));
                    if (username.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Username is required");
                        continue;
                    }
                    if (username.length() < 3) {
                        errors.add("Row " + (i+1) + ": Username must be at least 3 characters long");
                        continue;
                    }
                    if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
                        errors.add("Row " + (i+1) + ": Username can only contain letters, numbers, dots, underscores, and hyphens");
                        continue;
                    }
                    userProxy.setUserName(username);
                    
                    // Validate username uniqueness
                    if (isUsernameTaken(username, existingUsernames, processingUsernames)) {
                        errors.add("Row " + (i+1) + ": Username '" + username + "' already exists");
                        continue;
                    }
                    
                    // Name validation
                    String name = getCellValueAsString(row.getCell(1));
                    if (name.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Name is required");
                        continue;
                    }
                    userProxy.setName(name);
                    
                    // Email validation
                    String email = getCellValueAsString(row.getCell(2));
                    if (email.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Email is required");
                        continue;
                    }
                    // Basic email validation
                    if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        errors.add("Row " + (i+1) + ": Email should be valid");
                        continue;
                    }
                    userProxy.setEmail(email);
                    
                    // Validate email uniqueness
                    if (isEmailTaken(email, existingEmails, processingEmails)) {
                        errors.add("Row " + (i+1) + ": Email '" + email + "' already exists");
                        continue;
                    }
                    
                    // Gender validation
                    String genderStr = getCellValueAsString(row.getCell(3));
                    if (genderStr.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Gender is required");
                        continue;
                    }
                    userProxy.setGender(genderStr);
                    
                    Gender gender;
                    try {
                        gender = Gender.valueOf(genderStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        errors.add("Row " + (i+1) + ": Invalid gender value");
                        continue;
                    }
                    
                    // Date of birth validation and parsing
                    LocalDate dob;
                    Cell dobCell = row.getCell(4);
                    if (dobCell == null) {
                        errors.add("Row " + (i+1) + ": Date of birth is required");
                        continue;
                    }
                    
                    try {
                        if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
                            // Date stored as a numeric value
                            Date date = dobCell.getDateCellValue();
                            dob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        } else {
                            // Try parsing as string
                            dob = LocalDate.parse(getCellValueAsString(dobCell));
                        }
                        
                        // Validate date is in the past
                        if (!dob.isBefore(LocalDate.now())) {
                            errors.add("Row " + (i+1) + ": Date of birth must be in the past");
                            continue;
                        }
                        userProxy.setDob(dob);
                    } catch (Exception e) {
                        errors.add("Row " + (i+1) + ": Invalid date format");
                        continue;
                    }
                    
                    // Contact number validation
                    String contactNumber = getCellValueAsString(row.getCell(5));
                    if (contactNumber.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Contact number is required");
                        continue;
                    }
                    if (!contactNumber.matches("^[0-9]{10}$")) {
                        errors.add("Row " + (i+1) + ": Contact number must be 10 digits");
                        continue;
                    }
                    userProxy.setContactNumber(contactNumber);
                    
                    // Address validation
                    String address = getCellValueAsString(row.getCell(6));
                    if (address.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Address is required");
                        continue;
                    }
                    if (address.length() < 5) {
                        errors.add("Row " + (i+1) + ": Address must be at least 5 characters long");
                        continue;
                    }
                    userProxy.setAddress(address);
                    
                    // Pin code validation
                    String pinCode = getCellValueAsString(row.getCell(7));
                    if (pinCode.isEmpty()) {
                        errors.add("Row " + (i+1) + ": Pin code is required");
                        continue;
                    }
                    if (!pinCode.matches("^[0-9]{6}$")) {
                        errors.add("Row " + (i+1) + ": Pin code must be 6 digits");
                        continue;
                    }
                    userProxy.setPinCode(pinCode);
                    
                    // Set password (default)
                    String defaultPassword = "Welcome@123";
                    userProxy.setPassword(defaultPassword);
                    
                    // Validate the proxy using bean validation
                    Set<ConstraintViolation<UserProxy>> violations = validator.validate(userProxy);
                    if (!violations.isEmpty()) {
                        List<String> validationErrors = violations.stream()
                            .map(violation -> violation.getMessage())
                            .collect(Collectors.toList());
                        errors.add("Row " + (i+1) + ": " + String.join(", ", validationErrors));
                        continue;
                    }
                    
                    // If validation passed, create User entity
                    User user = new User();
                    user.setUserName(username);
                    user.setName(name);
                    user.setEmail(email);
                    user.setGender(gender);
                    user.setDob(dob);
                    user.setContactNumber(contactNumber);
                    user.setAddress(address);
                    user.setPinCode(pinCode);
                    
                    // Default values
                    user.setPassword(defaultPassword);
                    user.setRole(Set.of(userRole));
                    user.setNrole(userRole);
                    user.setActive(true);
                    
                    users.add(user);
                    processingUsernames.add(username);
                    processingEmails.add(email);
                    
                } catch (Exception e) {
                    errors.add("Row " + (i+1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("Error reading Excel file: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                
                // Handle integer vs decimal numbers
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    // It's an integer - format as long to avoid scientific notation
                    return String.valueOf((long)numericValue).trim();
                } else {
                    // It's a decimal - use default string representation
                    return String.valueOf(numericValue).trim();
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        // Try numeric formula result
                        double numValue = cell.getNumericCellValue();
                        if (numValue == Math.floor(numValue)) {
                            return String.valueOf((long)numValue).trim();
                        } else {
                            return String.valueOf(numValue).trim();
                        }
                    } catch (Exception ex) {
                        // If all else fails
                        return "";
                    }
                }
            case BLANK:
                return "";
            case ERROR:
                return "#ERROR";
            default:
                return "";
        }
    }

    private boolean isUsernameTaken(String username, Set<String> existingUsernames, Set<String> processingUsernames) {
        return existingUsernames.contains(username) || processingUsernames.contains(username);
    }

    private boolean isEmailTaken(String email, Set<String> existingEmails, Set<String> processingEmails) {
        return existingEmails.contains(email) || processingEmails.contains(email);
    }

}