package com.friendsbook.models;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.friendsbook.entity.Users;

public class UserInfoDetails implements UserDetails {

	private String username;
	private String password;

	public UserInfoDetails(Users user) {
		this.username = user.getUserEmail();
		this.password = user.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
