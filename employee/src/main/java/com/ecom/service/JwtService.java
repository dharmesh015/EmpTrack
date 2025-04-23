package com.ecom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.ecom.dao.UserDao;
import com.ecom.entity.JwtRequest;
import com.ecom.entity.JwtResponse;
import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;
import com.ecom.util.JwtUtil;
import com.ecom.util.MapperUtil;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService implements UserDetailsService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MapperUtil mapper;

	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;

	public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
		String userName = jwtRequest.getUserName();
		String userPassword = jwtRequest.getUserPassword();
		System.out.println("usernamw" + userName);
		authenticate(userName, userPassword);
		System.out.println("after createJwtToken reach");

		UserDetails userDetails = loadUserByUsername(userName);
		String newGeneratedToken = jwtUtil.generateToken(userDetails);

		User user = userDao.findByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userName));

		return new JwtResponse(user, newGeneratedToken);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				getAuthority(user));
	}

	private Set<SimpleGrantedAuthority> getAuthority(User user) {
		return user.getRole().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
				.collect(Collectors.toSet());
	}

	private void authenticate(String userName, String userPassword) throws Exception {

		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
		System.out.println("authentication method inside");
		if (!authenticate.isAuthenticated()) {

			throw new Exception("USER_DISABLED");
		}

	}

	public UserProxy getdata(String token) {
		String usrname = jwtUtil.getUsernameFromToken(token);
		Optional<User> byUserName = userDao.findByUserName(usrname);

		return mapper.convertValue(byUserName.get(), UserProxy.class);
	}
}
