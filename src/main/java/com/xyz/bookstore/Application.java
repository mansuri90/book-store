package com.xyz.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hadi on 2/8/20.
 */

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan({
        "com.xyz.bookstore.config",
        "com.xyz.bookstore.controller",
        "com.xyz.bookstore.repository",
        "com.xyz.bookstore.exceptionhandler",
        "com.xyz.bookstore.service"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
