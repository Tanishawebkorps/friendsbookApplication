package com.friendsbook.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.friendsbook.DTO.CommentsDTO;
import com.friendsbook.DTO.PostDTO;
import com.friendsbook.entity.Followings;
import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.FollowersRepository;
import com.friendsbook.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	FollowersRepository followerRepository;

	@Autowired
	private UserService userService;

	@Autowired
	CommentService commentService;
	@Autowired
	LikeService likeService;

	public Map<String, Object> uploadPost(String userId, MultipartFile file, String caption) throws IOException {
		Users user = userService.getUserByUserId(userId);
		if (user == null) {
			throw new RuntimeException("User not found with id: " + userId);
		}

		byte[] imageBytes = file.getBytes();

		Posts post = new Posts();
		post.setCaption(caption);
		post.setImage(imageBytes);
		post.setUser(user);
		post.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		Posts savedPost = postRepository.save(post);

		Map<String, Object> response = new HashMap<>();
		response.put("postId", savedPost.getPostId());
		response.put("imageUrl", "/post/" + savedPost.getPostId());

		return response;
	}

	@Transactional
	public List<PostDTO> getPostsFromFollowings(String userId) {
		Users user = userService.getUserByUserId(userId);

		if (user == null)
			return Collections.emptyList();

		Set<Followings> followings = user.getFollowings();

		if (followings == null || followings.isEmpty()) {
			return Collections.emptyList();
		}
		List<Users> followingUsers = followings.stream().map(Followings::getFollowedByUser)
				.collect(Collectors.toList());

		List<Posts> posts = postRepository.findByUserInOrderByCreatedAtDesc(followingUsers);

		if (posts == null || posts.isEmpty()) {
			return Collections.emptyList();
		}

		List<PostDTO> postDTOs = posts.stream().map(post -> {
			List<CommentsDTO> commentDTOs = commentService.getCommentsByPost(post.getPostId());
			Long likeCount = likeService.getLikeCount(post.getPostId());
			return new PostDTO(post.getPostId(), post.getUser().getUserId(), post.getCaption(), post.getImage(),
					commentDTOs, likeCount);
		}).collect(Collectors.toList());
		return postDTOs;
	}

	public List<PostDTO> getAllPostsByUser(String userId) {

		Users user = userService.getUserByUserId(userId);
		if (user != null) {
			List<Posts> posts = postRepository.findByUser(user);
			return posts.stream().map(post -> {
				List<CommentsDTO> commentDTOs = commentService.getCommentsByPost(post.getPostId());
				Long likeCount = likeService.getLikeCount(post.getPostId());
				String getUser = post.getUser().getUserId();
				return new PostDTO(post.getPostId(), getUser, post.getCaption(), post.getImage(), commentDTOs,
						likeCount);
			}).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public byte[] getPost(Long id) {
		Optional<Posts> image = postRepository.findById(id);
		return image.map(Posts::getImage).orElse(null);
	}

	public long getPostCountForUser(Users user) {
		return postRepository.countByUser(user);
	}

	public Posts findById(long postId) {
		return postRepository.findByPostId(postId);
	}
}
