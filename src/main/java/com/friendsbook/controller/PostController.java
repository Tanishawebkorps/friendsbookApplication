package com.friendsbook.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.friendsbook.DTO.PostDTO;
import com.friendsbook.entity.Users;
import com.friendsbook.service.LikeService;
import com.friendsbook.service.PostService;
import com.friendsbook.service.UserService;

@RestController
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@Autowired
	private LikeService likeService;

	@PostMapping("/posts/{userId}")
	public ResponseEntity<Map<String, Object>> uploadPost(@PathVariable String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "caption", required = false) String caption) {
		try {
			Map<String, Object> response = postService.uploadPost(userId, file, caption);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/followings/posts/{userId}")
	public ResponseEntity<List<PostDTO>> getPostsFromFollowings(@PathVariable String userId) {

		List<PostDTO> postDTOs = postService.getPostsFromFollowings(userId);

		if (postDTOs == null || postDTOs.isEmpty()) {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(postDTOs, HttpStatus.OK);
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<byte[]> getPost(@PathVariable Long postId) {
		byte[] postData = postService.getPost(postId);
		if (postData != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "image/jpeg");
			return new ResponseEntity<>(postData, headers, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/posts/count/{userId}")
	public long getPostCount(@PathVariable String userId) {
		Users user = new Users();
		user.setUserId(userId);
		return postService.getPostCountForUser(user);
	}

	@GetMapping("/userPosts")
	@ResponseBody
	public ResponseEntity<List<PostDTO>> getUserPosts(@RequestParam String userId) {
		Users user = userService.getUserByUserId(userId);
		if (user != null) {
			List<PostDTO> posts = postService.getAllPostsByUser(userId);
			return ResponseEntity.ok(posts);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/like/{postId}/{userId}")
	public ResponseEntity<String> likePost(@PathVariable Long postId, @PathVariable String userId) {
		String response = likeService.likePost(postId, userId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/like-count/{postId}")
	public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
		Long likeCount = likeService.getLikeCount(postId);
		return ResponseEntity.ok(likeCount);
	}

}
