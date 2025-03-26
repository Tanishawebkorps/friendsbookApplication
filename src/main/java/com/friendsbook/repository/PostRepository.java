package com.friendsbook.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;

@Repository
public interface PostRepository extends CrudRepository<Posts,Long>{
	List<Posts> findByUser(Users user);
	Posts findByPostId(long postId);
	List<Posts> findByUserInOrderByCreatedAtDesc(List<Users> users);
	long countByUser(Users user);
}
