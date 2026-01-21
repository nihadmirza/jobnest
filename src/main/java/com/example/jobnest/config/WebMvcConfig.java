package com.example.jobnest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/to the file system uploads directory
        Path uploadPath = Paths.get("src/main/resources/static/uploads");

        // Create directory if it doesn't exist
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            log.error("Failed to create uploads directory: ", e);
        }

        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        // Ensure the path ends with a separator and handle Windows paths
        String uploadPathString = uploadAbsolutePath.replace("\\", "/");
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
