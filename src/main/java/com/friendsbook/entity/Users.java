package com.friendsbook.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
	@Id
	@Column(name = "userId", length = 30)
	private String userId;

	@Column(name = "userName", nullable = false, length = 30)
	private String name;

	private String password;

	@Column(name = "userEmail", nullable = false, length = 40, unique = true)
	private String userEmail;

	@Column(name = "userBio", nullable = true, length = 50)
	private String userBio;

	private boolean scope;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "userImageId", referencedColumnName = "id")
	private UserImage profile;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private Set<Posts> posts = new HashSet<>();

	@OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JsonManagedReference
	private Set<Followers> followers = new HashSet<>();

	@OneToMany(mappedBy = "following", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JsonManagedReference
	private Set<Followings> followings = new HashSet<>();

}
