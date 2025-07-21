package com.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {


@Bean
WebMvcConfigurer corsConfigurer() {
   return new WebMvcConfigurer() {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/ws/**") // Map specific WebSocket endpoint
                   .allowedOrigins("*") // Allow requests from any origin
                   .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specified HTTP methods
                   .allowedHeaders("*") // Allow specified headers
                   .allowCredentials(false) // Disallow sending credentials like cookies
                   .maxAge(3600); // Set max age of pre-flight requests
       }
   };
}}
