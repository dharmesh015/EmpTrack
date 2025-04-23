package com.ecom.service.impl;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@Service
public class CaptchaService {
    
    private final Cage cage = new GCage();
    private final Random random = new Random();
    
    // Generate a random captcha text (alphanumeric, length 6)
    public String generateCaptchaText() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }
        return captcha.toString();
    }
    
    // Generate captcha image as Base64 string
    public String generateCaptchaImage(String captchaText) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            cage.draw(captchaText, outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }
}