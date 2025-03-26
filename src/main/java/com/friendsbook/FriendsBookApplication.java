package com.friendsbook;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.friendsbook.entity.Followers;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.UserRepository;
import com.friendsbook.repository.FollowersRepository;
@SpringBootApplication
public class FriendsBookApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FriendsBookApplication.class, args);
		UserRepository userRepository = context.getBean(UserRepository.class);
		FollowersRepository followersRepository = context.getBean(FollowersRepository.class);

	}

}
