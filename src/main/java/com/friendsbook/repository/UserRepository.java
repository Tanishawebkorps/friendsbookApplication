package com.friendsbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Users;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends CrudRepository<Users, String> {
	Optional<Users> findByUserEmail(String userEmail);
}