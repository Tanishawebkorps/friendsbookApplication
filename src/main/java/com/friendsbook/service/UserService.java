package com.friendsbook.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.friendsbook.entity.Captchaa;
import com.friendsbook.entity.Users;
import com.friendsbook.helper.PasswordValidator;
import com.friendsbook.repository.ImageRepository;
import com.friendsbook.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository userImageRepository;

	@Autowired
	private PasswordEncoder encoder;

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

	public boolean isEmailAlreadyRegistered(String userEmail) {
		Users getByEmail = getUserByUserEmail(userEmail);
		return getByEmail != null;
	}

	public boolean isCaptchaValid(String captchaInput, String correctCaptcha) {
		return captchaInput.equalsIgnoreCase(correctCaptcha);
	}

	public void generateAndSetNewCaptcha(HttpSession session, Model model) {
		Captchaa newCaptcha = CaptchaGenerator.getCaptcha();
		session.setAttribute("correctCaptcha", newCaptcha.getHiddenCaptcha());
		model.addAttribute("captcha", newCaptcha);
	}

	public boolean isPasswordValid(String password) {
		return PasswordValidator.isValidPassword(password);
	}

	public boolean registerUser(String userName, String userEmail, String password) {
		String userId = generateUserId(userName);
		Users getUser = getUserByUserId(userId);
		while (getUser != null) {
			userId = generateUserId(userName);
			getUser = getUserByUserId(userId);
		}

		Users user = new Users();
		user.setUserId(userId);
		user.setName(userName);
		user.setUserEmail(userEmail);
		user.setPassword(encoder.encode(password));
		user.setUserBio("");

		saveUser(user);
		return true;
	}

	public void saveUser(Users user) {
		userRepository.save(user);
	}

}
