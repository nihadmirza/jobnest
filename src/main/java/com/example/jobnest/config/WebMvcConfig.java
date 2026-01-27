package com.example.jobnest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Map /uploads/** to a writable file system directory (NOT inside classpath resources)
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Create directory if it doesn't exist
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            log.error("Failed to create uploads directory: ", e);
        }

        // Ensure the path ends with a separator and handle Windows paths
        String uploadPathString = uploadPath.toString().replace("\\", "/");
        if (!uploadPathString.endsWith("/")) {
            uploadPathString += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPathString)
                .setCachePeriod(3600); // Cache for 1 hour

        // Explicitly map WebJars (Spring Boot usually does this, but this is a safety
        // net)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
