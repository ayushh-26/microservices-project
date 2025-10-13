package com.project.library.Users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class UserServiceSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())       // Disable CSRF
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all requests
            .httpBasic(httpBasic -> httpBasic.disable());  // Disable default HTTP Basic Auth

        return http.build();
    }
}

