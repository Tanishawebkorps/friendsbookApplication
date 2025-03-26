package com.friendsbook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendsbook.DTO.CommentsDTO;
import com.friendsbook.entity.Comments;
import com.friendsbook.service.CommentService;

@RestController
public class CommentController {

	@Autowired
	CommentService commentService;

	@PostMapping("/addcomment/{postId}/{userId}/{commentContent}")
	public ResponseEntity<String> addComment(@PathVariable Long postId, @PathVariable String userId,
			@PathVariable String commentContent) {
		String response = commentService.addComment(postId, userId, commentContent);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/getComment/{postId}")
	public ResponseEntity<List<CommentsDTO>> getCommentsByPost(@PathVariable Long postId) {
		List<CommentsDTO> comments = commentService.getCommentsByPost(postId);
		return ResponseEntity.ok(comments);
	}
}
