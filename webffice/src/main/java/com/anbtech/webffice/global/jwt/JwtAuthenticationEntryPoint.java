package com.anbtech.webffice.global.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, jakarta.servlet.ServletException {
	    System.out.print("Error Response = " + HttpServletResponse.SC_UNAUTHORIZED);
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		response.sendRedirect("/404");
		
	}

}