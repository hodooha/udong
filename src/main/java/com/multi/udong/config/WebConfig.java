package com.multi.udong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

/**
 * The type Web config.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Add resource handlers.
     *
     * @param registry the registry
     * @since 2024 -08-13
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userHome = System.getProperty("user.home");
        String uploadDir = "udongUploads";
        String uploadPath = Paths.get(userHome, uploadDir).toAbsolutePath().toString();
        String fileUploadPath = uploadPath + File.separator;

        registry.addResourceHandler("/udongUploads/**")
                .addResourceLocations("file:" + fileUploadPath);
    }
}
