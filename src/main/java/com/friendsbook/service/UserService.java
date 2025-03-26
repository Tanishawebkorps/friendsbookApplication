package com.friendsbook.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendsbook.entity.Users;
import com.friendsbook.repository.ImageRepository;
import com.friendsbook.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository userImageRepository;

	public static String generateUserId(String userName) {
		StringBuilder username = new StringBuilder();

		if (userName != null && !userName.isEmpty()) {

			int endIndex = Math.min(userName.length(), 6);
			username.append(userName.substring(0, 1).toUpperCase());
			username.append(userName.substring(1, endIndex).toLowerCase());
		}

		Random rand = new Random();
		for (int i = 0; i < 3; i++) {
			username.append(rand.nextInt(10));
		}

		return username.toString();
	}

	public Users getUserByUserId(String userId) {
		Optional<Users> optionalUser = this.userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();

			return user;
		}

			return null;

	}

	public Users getUserByUserEmail(String userEmail) {
		Optional<Users> optionalUser = this.userRepository.findByUserEmail(userEmail);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();

			return user;
		} else {

			return null;
		}
	}

	public Users getUserIdByUserEmail(String userEmail) {
		return userRepository.findUserIdByUserEmail(userEmail);
	}

	public Users saveUser(Users user) {
		return userRepository.save(user);
	}

	public List<Users> getAllUsers() {
		return (List<Users>) userRepository.findAll();
	}

	public boolean updateUserBio(String userEmail, String newBio) {
		Users user = getUserByUserEmail(userEmail);
		if (user == null) {
			return false;
		}
		user.setUserBio(newBio);
		userRepository.save(user);
		return true;
	}

}
