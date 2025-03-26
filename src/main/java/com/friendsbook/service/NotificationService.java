package com.friendsbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendsbook.entity.Followers;
import com.friendsbook.entity.FriendRequest;
import com.friendsbook.entity.Notification;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.FollowersRepository;
import com.friendsbook.repository.FollowingsRepository;
import com.friendsbook.repository.FriendRequestRepository;
import com.friendsbook.repository.NotificationRepository;
import com.friendsbook.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private FollowersRepository followersRepository;

	@Autowired
	private FollowingsRepository followingsRepository;

	@Autowired
	FollowService followService;

	public String sendFriendRequest(String senderId, String recipientId) {
		Users sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
		Users recipient = userRepository.findById(recipientId)
				.orElseThrow(() -> new RuntimeException("Recipient not found"));

		if (notificationRepository.existsByUserAndSender(recipient, sender)) {
			return "Friend request already sent.";
		}

		FriendRequest newRequest = new FriendRequest();
		newRequest.setSender(sender);
		newRequest.setRecipient(recipient);
		newRequest.setStatus("pending");
		friendRequestRepository.save(newRequest);

		Notification notification = new Notification();
		notification.setSender(sender);
		notification.setUser(recipient);
		notification.setMessage(sender.getUserId() + " sent you a friend request.");
		notification.setStatus("pending");
		notificationRepository.save(notification);

		return "Friend request sent successfully.";
	}

	public String cancelFriendRequest(String senderId, String recipientId) {
		Users sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
		Users recipient = userRepository.findById(recipientId)
				.orElseThrow(() -> new RuntimeException("Recipient not found"));
		FriendRequest existingRequest = friendRequestRepository.findBySenderAndRecipientAndStatus(sender, recipient,
				"pending");
		if (existingRequest == null) {
			return "No pending friend request found to cancel.";
		}
		friendRequestRepository.delete(existingRequest);
		Notification existingNotification = notificationRepository.findBySenderAndUserAndMessage(sender, recipient,
				sender.getUserId() + " sent you a friend request.");

		if (existingNotification != null) {
			notificationRepository.delete(existingNotification);
		}
		return "Friend request canceled successfully.";
	}

	public String acceptFriendRequest(Long notificationId, String recipientId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		Users recipient = userRepository.findById(recipientId)
				.orElseThrow(() -> new RuntimeException("Recipient not found"));

		Users sender = notification.getSender();
		if (!followersRepository.existsByFollowerAndFollowedUser(sender, recipient)) {
			Followers follower = new Followers();
			follower.setFollower(sender);
			follower.setFollowedUser(recipient);
			followersRepository.save(follower);
		}

		notification.setStatus("accepted");
		notificationRepository.delete(notification);

		return "Friend request accepted.";
	}

	public String declineFriendRequest(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		notification.setStatus("declined");
		notificationRepository.delete(notification);

		return "Friend request declined.";
	}

	public List<Notification> getNotificationsByUserAndStatus(Users user, String status) {
		return notificationRepository.findByUserAndStatus(user, status);
	}

	@Transactional
	public void updateNotificationStatus(Long notificationId, String status) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		if (status.equalsIgnoreCase("accepted")) {

			Users user = notification.getUser();
			Users sender = notification.getSender();
			followService.followUser(sender, user);
			notificationRepository.delete(notification);
		} else if (status.equalsIgnoreCase("declined")) {

			notificationRepository.delete(notification);

		}
	}

	public List<Object[]> getNotificationCountPerUser() {
		return notificationRepository.countNotificationsPerUser();
	}

	public void saveNotification(Notification notification) {
		notificationRepository.save(notification);
	}

	public boolean hasNotificationFromSenderToUser(Users user, Users sender) {
		return notificationRepository.existsByUserAndSender(user, sender);
	}

}
