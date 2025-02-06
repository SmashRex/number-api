package com.hng.number_api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    // Create a Bean
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allows CORS requests for all endpoints ('/**')
                registry.addMapping("/**") // Apply to all APIs
                        .allowedOrigins("*") // Allow all origins (can be restricted to specific origins later)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("*") // Allow all headers in the request
                        .allowCredentials(true); // Allow sending credentials (like cookies, authorization headers)
            }
        };
    }
}
