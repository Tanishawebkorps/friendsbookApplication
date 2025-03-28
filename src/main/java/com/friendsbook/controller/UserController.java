package com.friendsbook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendsbook.DTO.UserRegistrationDTO;
import com.friendsbook.DTO.UsersDTO;
import com.friendsbook.entity.Captchaa;
import com.friendsbook.entity.Users;
import com.friendsbook.helper.PasswordValidator;
import com.friendsbook.service.CaptchaGenerator;
import com.friendsbook.service.JwtService;
import com.friendsbook.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	CaptchaGenerator captchaGenerator;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder encoder;

	@GetMapping("/login")
	public String displayLogin() {
		return "login.html";
	}

	@PostMapping("/updateBio")
	public ResponseEntity<Boolean> updateBio(@RequestParam String userEmail, @RequestParam String userBio) {
		boolean isUpdated = userService.updateUserBio(userEmail, userBio);
		if (isUpdated) {
			return ResponseEntity.ok(true);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
	}

	@GetMapping("/logoutCooki")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextHolder.clearContext();
		Cookie cookie = new Cookie("jwt", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "redirect:/home";
	}

	@PostMapping("/generateToken")
	public String authenticateAndGetToken(@RequestParam(required = true) String userEmail,
			@RequestParam(required = true) String password, HttpSession session, HttpServletResponse response,
			Model model) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userEmail, password));
		if (authentication.isAuthenticated()) {
			String token = jwtService.generateToken(userEmail);
			Cookie cookie = new Cookie("jwt", token);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			cookie.setMaxAge(7 * 24 * 60 * 60);
			response.addCookie(cookie);

			Users user = userService.getUserByUserEmail(userEmail);
			model.addAttribute("userId", user.getUserId());
			model.addAttribute("userEmail", user.getUserEmail());
			return "redirect:/images/profile?userId=" + user.getUserId();

		} else {
			throw new UsernameNotFoundException("Invalid user request!");
		}
	}

	@GetMapping("/register")
	public String displayRegister(Model model, HttpSession session) {
		Captchaa captchaData = CaptchaGenerator.getCaptcha();

		session.setAttribute("correctCaptcha", captchaData.getHiddenCaptcha());

		model.addAttribute("captcha", captchaData);
		return "register.html";
	}

	@GetMapping("/search")
	public ResponseEntity<UsersDTO> getUserById(@RequestParam String userId) {
		Users user = userService.getUserByUserId(userId);

		if (user != null) {

			UsersDTO userDTO = new UsersDTO(user.getUserId(), user.getUserEmail());
			return ResponseEntity.ok(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping("/reguser")
	public ResponseEntity<Map<String, String>> userRegistrationHandler(@RequestBody UserRegistrationDTO registrationDTO,
			HttpSession session, Model model) {

		String correctCaptcha = (String) session.getAttribute("correctCaptcha");

		if (userService.isEmailAlreadyRegistered(registrationDTO.getUserEmail())) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Email is already registered. Please use a different email.");
			response.put("redirectUrl", "/friendsbook/register");
			return ResponseEntity.ok(response);
		}
		if (!PasswordValidator.isValidPassword(registrationDTO.getPassword())) {
			Map<String, String> response = new HashMap<>();
			response.put("message",
					"Please enter a valid password with 8 characters including numbers, upper case, lower case, and special symbols.");
			response.put("redirectUrl", "/friendsbook/register");
			return ResponseEntity.ok(response);
		}
		Captchaa captcha = registrationDTO.getCaptcha();
		if (userService.isCaptchaValid(captcha.getCaptcha(), correctCaptcha)) {
			model.addAttribute("message", "CAPTCHA verification successful!");
			if (userService.registerUser(registrationDTO.getUserName(), registrationDTO.getUserEmail(),
					registrationDTO.getPassword())) {
				Map<String, String> response = new HashMap<>();
				response.put("message", "Registration successful");
				response.put("redirectUrl", "/friendsbook/login");
				return ResponseEntity.ok(response);
			} else {
				Map<String, String> response = new HashMap<>();
				response.put("message", "User registration failed. Please try again.");
				response.put("redirectUrl", "/friendsbook/register");
				return ResponseEntity.ok(response);
			}
		} else {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Incorrect CAPTCHA. Please try again.");
			response.put("redirectUrl", "/friendsbook/register");
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping("/users")
	public ResponseEntity<List<UsersDTO>> getAllUsers() {
		List<Users> users = userService.getAllUsers();

		if (users.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		List<UsersDTO> userDTOs = users.stream().map(user -> new UsersDTO(user.getUserId(), user.getUserEmail()))
				.collect(Collectors.toList());

		return ResponseEntity.ok(userDTOs);
	}

	@GetMapping("/getusers/{userId}/{currentUserId}")
	public String getUserByUserId(@PathVariable String userId, @PathVariable String currentUserId, Model model) {
		Users user = userService.getUserByUserId(userId);
		Users currentUser = userService.getUserByUserId(currentUserId);
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("currentUser", currentUser);
			return "userProfile";
		} else {

			return "redirect:/error";
		}
	}

}
