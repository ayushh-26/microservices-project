package com.project.library.configurationserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigurationserverApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String mode = dotenv.get("CONFIG_MODE");

        SpringApplication app = new SpringApplication(ConfigurationserverApplication.class);

        if ("native".equalsIgnoreCase(mode)) {
            System.out.println("Running in NATIVE mode (local folder)");
            app.setAdditionalProfiles("native");
        } else {
            System.out.println("Running in GIT mode (GitHub repo)");
            app.setAdditionalProfiles("git");
        }

        app.run(args);
    }
}
