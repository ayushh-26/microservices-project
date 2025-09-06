package com.project.library.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator routes(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("userservice", r -> r.path("/users/**")
                                                .uri("lb://userservice"))
                                .route("bookservice", r -> r.path("/books/**")
                                                .uri("lb://bookservice"))
                                .route("issueservice", r -> r.path("/issues/**")
                                                .uri("lb://issueservice"))
                                .route("reviewservice", r -> r.path("/reviews/**")
                                                .uri("lb://reviewservice"))
                                .build();
        }
}
