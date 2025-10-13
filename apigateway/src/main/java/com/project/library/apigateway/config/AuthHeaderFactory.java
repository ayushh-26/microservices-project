package com.project.library.apigateway.config;

import com.project.library.apigateway.model.ServiceCredentials;
import com.project.library.apigateway.repository.ServiceCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AuthHeaderFactory {

    @Autowired
    private ServiceCredentialsRepository credentialsRepo;

    public String buildAuthHeader(String serviceName) {
        ServiceCredentials creds = credentialsRepo.getByServiceName(serviceName);
        String auth = creds.getUsername() + ":" + creds.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    public String getSharedSecret(String serviceName) {
        ServiceCredentials creds = credentialsRepo.getByServiceName(serviceName);
        return creds.getSharedSecret();
    }
}
