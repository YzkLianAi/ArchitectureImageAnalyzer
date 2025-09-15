package com.example.architectureimageanalyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching  // 启用缓存
public class ArchitectureImageAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchitectureImageAnalyzerApplication.class, args);
        log.info("项目启动成功...");
    }

}
