package com.friendsbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Comments;
import com.friendsbook.entity.Posts;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
	List<Comments> findAllByPost(Posts post);
}
