package com.friendsbook.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(Exception.class)
	public String handleGeneralException(Exception ex, Model model) {
		model.addAttribute("exception", ex.getMessage());
		return "error";
	}
}