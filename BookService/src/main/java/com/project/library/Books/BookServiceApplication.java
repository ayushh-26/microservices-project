package com.project.library.Books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.library.Books")
@EnableDiscoveryClient
public class BookServiceApplication {
    public static void main(String[] args) {

        SpringApplication.run(BookServiceApplication.class, args);
    }
}
