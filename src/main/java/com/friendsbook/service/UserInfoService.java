package com.friendsbook.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.friendsbook.entity.Users;
import com.friendsbook.models.UserInfoDetails;
import com.friendsbook.repository.UserRepository;

@Primary
@Service
public class UserInfoService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> userDetail = repository.findByUserEmail(username);
		return userDetail.map(UserInfoDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public String addUser(Users user) {
		user.setPassword(encoder.encode(user.getPassword()));
		repository.save(user);
		return "User Added Successfully";
	}
}
