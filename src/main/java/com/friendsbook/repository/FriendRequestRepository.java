package com.friendsbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.FriendRequest;
import com.friendsbook.entity.Users;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

	FriendRequest findBySenderAndRecipientAndStatus(Users sender, Users recipient, String status);
}
