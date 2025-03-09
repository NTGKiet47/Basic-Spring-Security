package com.example.Combine.Security.Methods.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UsernameAuthHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    public UsernameAuthHandler(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = User.withUsername(authentication.getName()).password("").authorities(authentication.getAuthorities()).build();

        var jwtToken = jwtService.generateToken(userDetails);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);

        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        response.setStatus(HttpStatus.OK.value());

        log.info("User {} has logged in", authentication.getName());
    }
}
