package com.friendsbook.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JWTRequest {
	private String userEmail;
	private String Password;

}
