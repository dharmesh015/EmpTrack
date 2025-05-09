package com.ecom.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.entity.ApiResponse;
import com.ecom.entity.Role;
import com.ecom.proxy.UserProxy;
import com.ecom.service.AdminService;
import com.ecom.service.UserService;
import com.ecom.util.MapperUtil;

@RestController
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MapperUtil mapper;
   
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/getUsersByRole")
    public ResponseEntity<?> getUsersByRole(
            @RequestParam String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
    	
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            
            PageRequest pageable = PageRequest.of(page, size, sort);
            Page<?> pageData = adminService.getUsersByRole(role, pageable);
            
            return ResponseEntity.ok(pageData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Error", "Failed to retrieve users by role: " + e.getMessage()));
        }
    }
    
  
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<?> deleteUserByUserName(@PathVariable("userName") String userName) {
        try {
            adminService.deleteUser(userName);
            return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(), "Success", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Error", "Failed to delete user: " + e.getMessage()));
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UserProxy userProxy) {
        try {
            String result = adminService.updateUser(userProxy);
            return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(), "Success", "User updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Error", "Failed to update user: " + e.getMessage()));
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getuser/{name}")
    public ResponseEntity<?> getUser(@PathVariable("name") String name) {
        try {
            UserProxy user = adminService.getuser(name);
            if (user != null) {
                return ResponseEntity.ok(
                    new ApiResponse(HttpStatus.OK.value(), "Success", "User retrieved successfully", user));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), 
                        "NotFound", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Error", "Failed to retrieve user: " + e.getMessage()));
        }
    }
    
    
    
    @GetMapping("/generate-fake-users")
    public ResponseEntity<?> generateFakeUsers() {
        try {
            String message = adminService.generateFakeUsers();
            return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(), "Success", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Error", "Failed to generate fake users: " + e.getMessage()));
        }
    }
    
    
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        ByteArrayOutputStream outputStream = userService.exportToExcel();
        byte[] bytes = outputStream.toByteArray();
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users_data.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    

    
  
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadBlankExcelTemplate() {
        ByteArrayOutputStream outputStream = userService.generateBlankExcelTemplate();
        byte[] bytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users_template.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/searchUsersByRole")
    public ResponseEntity<?> searchUsersByRole(
            @RequestParam String role,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort sort = direction.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            PageRequest pageable = PageRequest.of(page, size, sort);
            
            Page<?> pageData = adminService.searchUsersByRole(role, query, pageable);
            return ResponseEntity.ok(pageData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error", "Failed to search users by role: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        String contentType = file.getContentType();
        if (!(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"))) {
            return ResponseEntity.badRequest().body("Only Excel or CSV files are allowed.");
        }

        try {
            List<String> errors = userService.importUsersFromFile(file);
            if (errors.isEmpty()) {
                return ResponseEntity.ok("Users imported successfully.");
            } else {
                // Return a more detailed response with all errors
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Some users could not be imported");
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error importing users: " + e.getMessage());
        }
    }

}