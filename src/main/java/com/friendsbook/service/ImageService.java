package com.friendsbook.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.friendsbook.entity.UserImage;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.ImageRepository;
import com.friendsbook.repository.UserRepository;

@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	public UserImage saveImage(String userId, MultipartFile file) throws IOException {
		UserImage image = new UserImage();
		image.setFileName(file.getOriginalFilename());
		image.setImageData(file.getBytes());
		Users user = userService.getUserByUserId(userId);
		image = imageRepository.save(image);
		user.setProfile(image);
		userRepository.save(user);
		return image;
	}

	public byte[] getImage(Long id) {
		Optional<UserImage> image = imageRepository.findById(id);
		return image.map(UserImage::getImageData).orElse(null);
	}
}
