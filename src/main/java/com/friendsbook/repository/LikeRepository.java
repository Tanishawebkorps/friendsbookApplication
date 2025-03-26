package com.friendsbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
	boolean existsByPost_PostIdAndUser_UserId(Long postId, String userId);
    Long countByPost_PostId(Long postId);
}
