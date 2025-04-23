package com.ecom.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

	@PreAuthorize("hasRole('Admin')")
	@GetMapping({ "/getdata/{token}" })
	public UserProxy getdata(@PathVariable("token") String token) {
		return jwtService.getdata(token);

	}
	

	@PostMapping("/registerNewUser")
	public String registerNewUser(@RequestBody UserProxy user) {
		System.out.println("controler"+user.getPassword());
		return Service.registerNewUser(user);
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

}
