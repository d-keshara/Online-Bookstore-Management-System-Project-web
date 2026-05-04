package com.bookstore.online_bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This gets the absolute path to your images folder on your computer
        Path imageDir = Paths.get("src/main/resources/static/images");
        String imagePath = imageDir.toFile().getAbsolutePath();

        // This tells Spring: "If a webpage asks for /images/something.jpg, go look directly in this folder!"
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + imagePath + "/");
    }
}