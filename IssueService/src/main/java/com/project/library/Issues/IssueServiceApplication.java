package com.project.library.Issues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.library")
public class IssueServiceApplication {
    public static void main(String[] args) {

        SpringApplication.run(IssueServiceApplication.class, args);
    }
}
