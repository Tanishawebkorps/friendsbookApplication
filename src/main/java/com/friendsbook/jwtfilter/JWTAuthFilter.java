package com.friendsbook.jwtfilter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.friendsbook.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;

	@Autowired
	public JWTAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
			} else {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if ("jwt".equals(cookie.getName())) {
							token = cookie.getValue();
							break;
						}
					}
				}
			}

			if (token != null) {
				username = jwtService.extractUsername(token);
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (jwtService.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (io.jsonwebtoken.ExpiredJwtException ex) {
			System.out.println("JWT expired. Clearing cookie and redirecting to login.");
			Cookie expiredCookie = new Cookie("jwt", null);
			expiredCookie.setHttpOnly(true);
			expiredCookie.setMaxAge(0);
			expiredCookie.setPath("/");
			response.addCookie(expiredCookie);
			response.sendRedirect("/home");
			return;
		}
		filterChain.doFilter(request, response);
	}
}