package com.friendsbook.repository;

import com.friendsbook.entity.Followers;
import com.friendsbook.entity.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {
    List<Followers> findByFollowedUser(Users followedUser);
	List<Followers> findByFollowedUser_UserId(String followedUserId);
    List<Followers> findByFollower(Users follower);
    boolean existsByFollowerAndFollowedUser(Users follower, Users followedUser);
    Optional<Followers> findByFollowerAndFollowedUser(Users follower, Users followedUser);
    long countByFollowedUser(Users followedUser);
}