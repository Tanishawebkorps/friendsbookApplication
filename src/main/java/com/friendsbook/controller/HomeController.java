package com.friendsbook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendsbook.entity.Users;
import com.friendsbook.service.JwtService;
import com.friendsbook.service.UserService;

@Controller
public class HomeController {

	@GetMapping("/home")
	public String homeHandler() {
		return "login.html";
	}

	@GetMapping("/userhome")
	public String userHomeHandler() {
		return "home.html";
	}

}
