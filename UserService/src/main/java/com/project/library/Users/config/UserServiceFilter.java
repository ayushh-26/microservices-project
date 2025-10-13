package com.project.library.Users.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserServiceFilter extends OncePerRequestFilter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getSharedSecretFromDB() {
        String sql = "SELECT shared_secret FROM gateway_credentials WHERE service_name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{"userservice"}, String.class);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Check API Gateway Secret header
        String secretHeader = request.getHeader("X-API-GATEWAY-SECRET");
        String dbSecret = getSharedSecretFromDB();
        if (!dbSecret.equals(secretHeader)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid API Gateway Secret");
            return;
        }

        // 2. Check Basic Auth credentials sent from API Gateway
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing Authorization Header");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
        String decodedString = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
        String[] parts = decodedString.split(":", 2);
        String username = parts[0];
        String password = parts.length > 1 ? parts[1] : "";

        // Fetch correct username/password from DB
        String sql = "SELECT username, password FROM gateway_credentials WHERE service_name = ?";
        var creds = jdbcTemplate.queryForMap(sql, "userservice");
        String dbUsername = (String) creds.get("username");
        String dbPassword = (String) creds.get("password");

        if (!dbUsername.equals(username) || !dbPassword.equals(password)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid Credentials");
            return;
        }

        // All checks passed
        filterChain.doFilter(request, response);
    }
}
