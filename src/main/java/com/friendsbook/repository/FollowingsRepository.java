package com.friendsbook.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Followings;
import com.friendsbook.entity.Users;

@Repository
public interface FollowingsRepository extends JpaRepository<Followings, Long> {
    
	Set<Followings> findByFollowingUserId(String userId);
    List<Followings> findByFollowedByUser(Users followedByUser);
	List<Followings> findByFollowedByUser_UserId(String userId);
	List<Followings> findByFollowing(Users following);
	long countByFollowing(Users following);
    boolean existsByFollowingAndFollowedByUser(Users following, Users followedByUser);
    Optional<Followings> findByFollowingAndFollowedByUser(Users following, Users followedByUser);
}
