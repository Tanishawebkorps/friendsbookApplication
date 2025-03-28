package com.friendsbook.DTO;

import com.friendsbook.entity.Captchaa;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String userName;
    private String userEmail;
    private String password;
    private Captchaa captcha;  
}

