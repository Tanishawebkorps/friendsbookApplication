package com.friendsbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override 
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/register").setViewName("register");	
		registry.addViewController("/error").setViewName("error");
		registry.addViewController("/userProfile").setViewName("userProfile");
	}
	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/images/**").allowedOrigins("http://localhost:3000");
	    }
}
