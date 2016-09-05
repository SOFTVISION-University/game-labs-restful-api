package com.practicaSV.gameLabz.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@EnableAspectJAutoProxy
@ImportResource({"classpath:database.xml", "classpath:spring-mail.xml", "classpath:utils.xml"})
@SpringBootApplication(scanBasePackages = "com.practicaSV.gameLabz")
public class Application {

    public static void main(String[] args) {

        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            System.err.println("Failed to initialize server! " + e.getMessage());
            System.exit(1);
        }
    }
}
