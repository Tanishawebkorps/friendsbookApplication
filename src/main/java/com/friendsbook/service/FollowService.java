package com.friendsbook.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendsbook.entity.Followers;
import com.friendsbook.entity.Followings;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.FollowersRepository;
import com.friendsbook.repository.FollowingsRepository;
import com.friendsbook.repository.UserRepository;

@Service
public class FollowService {
	@Autowired
	private FollowersRepository followersRepository;
	@Autowired
	private FollowingsRepository followingsRepository;
	@Autowired
	private UserRepository usersRepository;

	public void followUser(Users follower, Users followedUser) {
		if (!followersRepository.existsByFollowerAndFollowedUser(follower, followedUser)) {
			Followers followers = new Followers();
			followers.setFollower(follower);
			followers.setFollowedUser(followedUser);
			followersRepository.save(followers);
		}
		if (!followingsRepository.existsByFollowingAndFollowedByUser(follower, followedUser)) {
			Followings followings = new Followings();
			followings.setFollowing(follower);
			followings.setFollowedByUser(followedUser);
			followingsRepository.save(followings);
		}
	}

	public void unfollowUser(String followerId, String followedUserId) {
		Users follower = usersRepository.findById(followerId).orElseThrow(() -> new RuntimeException("User not found"));
		Users followedUser = usersRepository.findById(followedUserId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Followers followers = followersRepository.findByFollowerAndFollowedUser(follower, followedUser)
				.orElseThrow(() -> new RuntimeException("Following relationship not found"));
		Followings followings = followingsRepository.findByFollowingAndFollowedByUser(follower, followedUser)
				.orElseThrow(() -> new RuntimeException("Following relationship not found"));

		followersRepository.delete(followers);
		followingsRepository.delete(followings);
	}

	public long getCountOfFollowers(String userId) {
		Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		return followersRepository.countByFollowedUser(user);
	}

	public long getCountOfFollowings(String userId) {
		Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		return followingsRepository.countByFollowing(user);
	}

	public List<Followers> getFollowersByUser(Users followedUser) {
		return followersRepository.findByFollowedUser(followedUser);
	}

	public Set<Followings> getFollowingsByUserId(String userId) {
		return followingsRepository.findByFollowingUserId(userId);
	}

	public boolean isFollowing(Users follower, Users followedUser) {
		return followersRepository.existsByFollowerAndFollowedUser(follower, followedUser);
	}
}
