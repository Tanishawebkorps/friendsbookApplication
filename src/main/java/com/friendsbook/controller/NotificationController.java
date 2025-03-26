package com.friendsbook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.friendsbook.entity.Notification;
import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;
import com.friendsbook.repository.NotificationRepository;
import com.friendsbook.service.NotificationService;
import com.friendsbook.service.PostService;
import com.friendsbook.service.UserService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping("/sendFriendRequest")
    public ResponseEntity<String> sendFriendRequest(@RequestParam String senderId, @RequestParam String recipientId) {
        String response = notificationService.sendFriendRequest(senderId, recipientId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/friend-requests/{senderId}/{recipientId}")
    public ResponseEntity<Map<String, String>> cancelFriendRequest(@PathVariable String senderId, @PathVariable String recipientId) {
        
        String response = notificationService.cancelFriendRequest(senderId, recipientId);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", response);  

        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/acceptFriendRequest")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam Long notificationId,
                                                      @RequestParam String recipientId) {
        String response = notificationService.acceptFriendRequest(notificationId, recipientId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/declineFriendRequest")
    public ResponseEntity<String> declineFriendRequest(@RequestParam Long notificationId) {
        String response = notificationService.declineFriendRequest(notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications/getSenderName")
    public String getSenderName(@RequestParam Long notificationId) {
        Notification notification = (notificationRepository.findById(notificationId)).get();
        Users sender = notification.getSender();
        return sender.getName();
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Notification> getNotifications(@PathVariable String userId, @PathVariable String status) {
        Users user = userService.getUserByUserId(userId);
        return notificationService.getNotificationsByUserAndStatus(user, status);
    }

    @PostMapping("/update-status/{notificationId}")
    public ResponseEntity<String> updateNotificationStatus(@PathVariable Long notificationId, @RequestParam String status) {
        notificationService.updateNotificationStatus(notificationId, status);
        return ResponseEntity.ok("updated");
    }

    @GetMapping("/notification/countPerUser")
    public List<Object[]> getNotificationCountPerUser() {
        return notificationService.getNotificationCountPerUser();
    }

    @PostMapping("/sendLikeNotification")
    public String sendLikeNotification(@RequestParam Long postId, @RequestParam String senderId) {
        try {

            Posts post = postService.findById(postId);
            Users sender = userService.getUserByUserId(senderId);
            Users postOwner = post.getUser();

            Notification notification = new Notification();
            notification.setMessage(sender.getName() + " liked your post!");
            notification.setStatus("pending");
            notification.setSender(sender);
            notification.setUser(postOwner);

            notificationService.saveNotification(notification);

            return " post like successfully Notification sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }

    @GetMapping("/check/follow")
    public boolean checkFollowRequest(@RequestParam String senderId, @RequestParam String recipientId) {

        Users sender = userService.getUserByUserId(senderId);
        Users recipient = userService.getUserByUserId(recipientId);

        if (sender == null || recipient == null)
            return false;
        return notificationService.hasNotificationFromSenderToUser(recipient, sender);
    }

}
