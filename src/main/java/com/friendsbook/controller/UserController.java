package com.friendsbook.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendsbook.entity.Captchaa;
import com.friendsbook.entity.Users;
import com.friendsbook.helper.PasswordValidator;
import com.friendsbook.service.CaptchaGenerator;
import com.friendsbook.service.ImageService;
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
	private ImageService imageService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder encoder;

	@RequestMapping("/login")
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

	@RequestMapping("/register")
	public String displayRegister(Model model, HttpSession session) {
		Captchaa captchaData = CaptchaGenerator.getCaptcha();

		session.setAttribute("correctCaptcha", captchaData.getHiddenCaptcha());

		model.addAttribute("captcha", captchaData);
		return "register.html";
	}

	@GetMapping("/search")
	public ResponseEntity<Users> getUserById(@RequestParam String userId) {
		Users user = userService.getUserByUserId(userId);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping("/reguser")
	public String userRegistrationHandler(@RequestParam(required = true) String userName,
			@RequestParam(required = true) String userEmail, @RequestParam(required = true) String password,
			@ModelAttribute("captcha") Captchaa captcha, HttpSession session, Model model) {

		String correctCaptcha = (String) session.getAttribute("correctCaptcha");

		Users getByEmail = userService.getUserByUserEmail(userEmail);
		if (getByEmail != null) {

			model.addAttribute("message", "Email is already registered. Please use a different email.");

			Captchaa newCaptcha = CaptchaGenerator.getCaptcha();
			session.setAttribute("correctCaptcha", newCaptcha.getHiddenCaptcha());
			model.addAttribute("captcha", newCaptcha);

			return "register";
		}
		if (!PasswordValidator.isValidPassword(password)) {
			model.addAttribute("message",
					"Please Enter Password with 8 character include numbers , upper case ,lower case , special symbol ");

			Captchaa newCaptcha = CaptchaGenerator.getCaptcha();
			session.setAttribute("correctCaptcha", newCaptcha.getHiddenCaptcha());
			model.addAttribute("captcha", newCaptcha);

			return "register";
		}
		if (captcha.getCaptcha().equalsIgnoreCase(correctCaptcha)) {
			model.addAttribute("message", "CAPTCHA verification successful!");

			String userId = userService.generateUserId(userName);
			Users getUser = userService.getUserByUserId(userId);

			while (getUser != null) {
				userId = userService.generateUserId(userName);
				getUser = userService.getUserByUserId(userId);
			}

			if (getUser == null) {
				Users user = new Users();
				user.setUserId(userId);
				user.setName(userName);
				user.setUserEmail(userEmail);
				user.setPassword(encoder.encode(password));
				user.setUserBio("");
				userService.saveUser(user);
			}

			return "login";
		} else {

			model.addAttribute("message", "Incorrect CAPTCHA. Please try again.");

			Captchaa newCaptcha = CaptchaGenerator.getCaptcha();
			session.setAttribute("correctCaptcha", newCaptcha.getHiddenCaptcha());
			model.addAttribute("captcha", newCaptcha);

			return "register";
		}
	}

	@GetMapping("/users")
	public ResponseEntity<List<Users>> getAllUsers() {
		List<Users> users = userService.getAllUsers();
		if (users.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/getusers/{userId}/{currentUserId}")
	public String getUserByUserId(@PathVariable String userId, @PathVariable String currentUserId, Model model) {
		Users user = userService.getUserByUserId(userId);
		Users currentUser = userService.getUserByUserId(currentUserId);
		System.out.println("current user : " + currentUser.getUserId());
		System.out.println("searched user : " + user.getUserId());
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("currentUser", currentUser);
			return "userProfile";
		} else {

			return "redirect:/error";
		}
	}

}
