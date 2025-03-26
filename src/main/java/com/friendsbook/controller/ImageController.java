package com.friendsbook.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.friendsbook.entity.UserImage;
import com.friendsbook.entity.Users;
import com.friendsbook.service.ImageService;
import com.friendsbook.service.UserService;

@CrossOrigin
@Controller
@RequestMapping("/images")
public class ImageController {

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

//     1... Upload Image
	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
			@RequestParam("userId") String userId) {
		try {
			UserImage savedImage = imageService.saveImage(userId, file);
			return ResponseEntity.ok("/images/" + savedImage.getId());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
		}
	}

	// Retrieve and Display Image
	@GetMapping("/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
		byte[] imageData = imageService.getImage(id);
		if (imageData != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "image/jpeg");
			return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//	display user profile 
	@GetMapping("/profile")
	public String getUserProfile(@RequestParam String userId, Model model) {

		Users user = userService.getUserByUserId(userId);
		String imageUrl = (user.getProfile() != null && user.getProfile().getId() != null)
				? "/images/" + user.getProfile().getId()
				: "/image/Default_pfp.jpeg";
		model.addAttribute("user", user);
		model.addAttribute("profileImageUrl", imageUrl);
		return "profilee";
	}

	@GetMapping("/profilephoto")
	@ResponseBody
	public String getUserProfilePhoto(@RequestParam String userId) {
		Users user = userService.getUserByUserId(userId);
		String imageUrl = (user.getProfile() != null && user.getProfile().getId() != null)
				? "/images/" + user.getProfile().getId()
				: "/image/Default_pfp.jpeg";
		return imageUrl;
	}

}
