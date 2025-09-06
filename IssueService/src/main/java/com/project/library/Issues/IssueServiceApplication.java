package com.project.library.Issues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.library")
@EnableDiscoveryClient
public class IssueServiceApplication {
    public static void main(String[] args) {

        SpringApplication.run(IssueServiceApplication.class, args);
    }
}
