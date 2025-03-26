package com.friendsbook.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendsbook.entity.Likes;
import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.LikeRepository;
import com.friendsbook.repository.PostRepository;
import com.friendsbook.repository.UserRepository;

@Service
public class LikeService {

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	public String likePost(Long postId, String userId) {

		Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (likeRepository.existsByPost_PostIdAndUser_UserId(postId, userId)) {
			return "You have already liked this post.";
		}
		Likes like = new Likes();
		like.setPost(post);
		like.setUser(user);
		likeRepository.save(like);

		post.getLikes().add(like);
		postRepository.save(post);

		return "Post liked successfully!";
	}

	public Long getLikeCount(Long postId) {
		return likeRepository.countByPost_PostId(postId);
	}
}
