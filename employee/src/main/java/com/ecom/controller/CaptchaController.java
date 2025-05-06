package com.ecom.controller;
//import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.cage.Cage;
import com.github.cage.GCage;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class CaptchaController {

    private final Cage cage = new GCage();
    
	@GetMapping(value = "/captcha", produces = MediaType.IMAGE_PNG_VALUE)
	public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
		String captchaToken = cage.getTokenGenerator().next();
		captchaToken = captchaToken.substring(0, 6);
		// Store CAPTCHA token in session
		session.setAttribute("captcha", captchaToken);
		

 
		response.setContentType("image/png");
		System.err.println("captcha--ok"+session.getAttribute(captchaToken));
		try {
			cage.draw(captchaToken, response.getOutputStream());
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
