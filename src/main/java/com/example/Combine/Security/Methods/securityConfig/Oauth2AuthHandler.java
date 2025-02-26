package com.example.Combine.Security.Methods.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Oauth2AuthHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public Oauth2AuthHandler(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        UserDetails userDetails = User.withUsername(oauth2User.getAttribute("email")).password("")
                .authorities(authentication.getAuthorities()).build();

        String jwtToken = jwtService.generateToken(userDetails);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);

        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        response.setStatus(HttpStatus.OK.value());
    }
}
