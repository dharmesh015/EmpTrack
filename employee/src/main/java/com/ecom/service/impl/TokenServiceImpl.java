package com.ecom.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecom.dao.UserDao;
import com.ecom.entity.User;
import com.ecom.service.TokenService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service

public class TokenServiceImpl implements TokenService {

	@Autowired
	private UserDao userdao;
	@Value("${app.jwt.reset.secret:skdnwjfkswmdnwdiejfbfuewddvqyvwyvwdjwdbjenweuweb}")
	private String jwtSecret;

	private final long EXPIRATION_TIME = 10 * 60 * 1000;

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode("skdnwjfkswmdnwdiejfbfuewddvqyvwyvwdjwdbjenweuweb");
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generatePasswordResetToken(String email, String password) {
		System.err.println("generatePasswordResetToken" + email);
		Map<String, Object> claims = new HashMap<>();
		claims.put("purpose", password);

		return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String validateToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

			String email = claims.getSubject();
			Optional<User> userobj = userdao.findByEmail(email);
			
			User user=userobj.get();
			if (!user.getPassword().equals(claims.get("purpose"))) {
				return null;
			}

			return claims.getSubject();
		} catch (Exception e) {
			// Token is invalid or expired
			return null;
		}
	}

	public String getEmail(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

}