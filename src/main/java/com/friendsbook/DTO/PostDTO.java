package com.friendsbook.DTO;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDTO {
	private Long postId;
	private String userId;
	private String caption;
	private byte[] image; // The URL to the image
	private List<CommentsDTO> comments;
	private Long likeCount;
	
}
