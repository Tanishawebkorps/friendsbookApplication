package com.friendsbook.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.friendsbook.DTO.FollowersDTO;
import com.friendsbook.DTO.FollowingsDTO;
import com.friendsbook.entity.Followers;
import com.friendsbook.entity.Followings;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.FollowingsRepository;
import com.friendsbook.service.FollowService;
import com.friendsbook.service.UserService;

@RestController
public class FollowController {

	@Autowired
	private FollowService followService;
	@Autowired
	private UserService userService;
	@Autowired
	private FollowingsRepository followingsRepository;

	@GetMapping("/{userId}/followers/count")
	public ResponseEntity<Long> getFollowersCount(@PathVariable String userId) {
		long followersCount = followService.getCountOfFollowers(userId);
		return ResponseEntity.ok(followersCount);
	}

	@GetMapping("/{userId}/followings/count")
	public ResponseEntity<Long> getFollowingsCount(@PathVariable String userId) {
		long followingsCount = followService.getCountOfFollowings(userId);
		return ResponseEntity.ok(followingsCount);
	}

	@GetMapping("/followers/{userId}")
	public ResponseEntity<List<FollowersDTO>> getFollowers(@PathVariable String userId) {
		Users user = userService.getUserByUserId(userId);
		List<Followers> followers = followService.getFollowersByUser(user);
		List<FollowersDTO> followersDTOs = followers.stream()
				.map(follower -> new FollowersDTO(follower.getFollower().getUserId())).collect(Collectors.toList());
		return ResponseEntity.ok(followersDTOs);
	}

	@GetMapping("/followings/{userId}")
	public Set<FollowingsDTO> getFollowings(@PathVariable String userId) {
		Set<Followings> followings = followingsRepository.findByFollowingUserId(userId);
		return followings.stream().map(following -> new FollowingsDTO(following.getFollowedByUser().getUserId()))
				.collect(Collectors.toSet());
	}

	@DeleteMapping("/{followerId}/unfollow/{followedUserId}")
	public ResponseEntity<String> unfollowUser(@PathVariable String followerId, @PathVariable String followedUserId) {
		try {
			followService.unfollowUser(followerId, followedUserId);
			return ResponseEntity.ok("Successfully unfollowed the user.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}

	@GetMapping("following/exists")
	public boolean checkIfFollowing(@RequestParam String followerId, @RequestParam String followedUserId) {
		Users follower = userService.getUserByUserId(followerId);
		Users followedUser = userService.getUserByUserId(followedUserId);
		return followService.isFollowing(follower, followedUser);
	}
}
