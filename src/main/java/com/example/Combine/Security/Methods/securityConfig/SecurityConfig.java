package com.example.Combine.Security.Methods.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOauth2UserService oAuth2UserServiceCustom,
                                           JwtService jwtService,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        UsernameAuthHandler usernameAuthSuccessHandler = new UsernameAuthHandler(jwtService,
                objectMapper);
        Oauth2AuthHandler oauth2AuthSuccessHandler = new Oauth2AuthHandler(jwtService, objectMapper);

        http.authorizeHttpRequests(req -> req
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/register").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login.successHandler(usernameAuthSuccessHandler)
                        .failureHandler(
                                new org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler()).permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").addLogoutHandler(new JwtLogoutHandler(jwtService)))
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserServiceCustom))
                        .successHandler(oauth2AuthSuccessHandler).failureUrl("/auth/login"))
                .exceptionHandling(e -> e.accessDeniedPage("/accessDeined"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Allow your frontend origin
        configuration.setAllowedMethods(List.of("*")); // Allow all HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (cookies, authorization headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }

}
