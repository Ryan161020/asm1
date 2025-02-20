package com.example.ASM1.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /images/** tới thư mục lưu ảnh trên server
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:C:/uploads/images/");
    }
}
