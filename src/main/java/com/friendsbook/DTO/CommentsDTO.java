package com.friendsbook.DTO;

import com.friendsbook.entity.Posts;
import com.friendsbook.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentsDTO {
	private Long commentId;
	private String content;
	private String userId;
}
