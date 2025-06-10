package com.example.SoundMate.config;  // 경로에 맞게 수정!

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // 개발 중에는 모든 출처 허용
            .allowedMethods("*") // GET, POST 등 모든 메서드 허용
    }
}