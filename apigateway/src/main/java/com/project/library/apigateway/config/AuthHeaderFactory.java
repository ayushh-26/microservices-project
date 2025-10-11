package com.project.library.apigateway.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthHeaderFactory {

    @Value("${userservice.auth.username}")
    String _UserUsername;
    @Value("${userservice.auth.password}")
    String _UserPassword;

    @Value("${bookservice.auth.username}")
    String _BookUsername;
    @Value("${bookservice.auth.password}")
    String _BookPassword;

    @Value("${issueservice.auth.username}")
    String _IssueUsername;
    @Value("${issueservice.auth.password}")
    String _IssuePassword;

    @Value("${reviewservice.auth.username}")
    String _ReviewUsername;
    @Value("${reviewservice.auth.password}")
    String _ReviewPassword;

    @Value("${apigateway.shared.secret}")
    String _SharedSecret;

    public String BuildAuthHeader(String serviceName) {
        String username = "";
        String password = "";

        // use .equals() for string comparison, not ==
        if ("userservice".equals(serviceName)) {
            username = _UserUsername;
            password = _UserPassword;
        } else if ("bookservice".equals(serviceName)) {
            username = _BookUsername;
            password = _BookPassword;
        } else if ("issueservice".equals(serviceName)) {
            username = _IssueUsername;
            password = _IssuePassword;
        } else if ("reviewservice".equals(serviceName)) {
            username = _ReviewUsername;
            password = _ReviewPassword;
        }

        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    public String getSharedSecret() {
        return _SharedSecret;
    }
}
