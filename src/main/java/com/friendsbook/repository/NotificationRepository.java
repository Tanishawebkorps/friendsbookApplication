package com.friendsbook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.friendsbook.entity.Notification;
import com.friendsbook.entity.Users;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Notification findBySenderAndUserAndMessage(Users sender, Users recipient, String message);

	Optional<Notification> findById(Long id);

	boolean existsByUserAndSender(Users user, Users sender);

	List<Notification> findByUserAndStatus(Users user, String status);

	@Query("SELECT n.user, COUNT(n) FROM Notification n GROUP BY n.user")
	List<Object[]> countNotificationsPerUser();
}
