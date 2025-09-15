package com.example.architectureimageanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {
    private VolcengineApiService volcengineApiService;

    // 使用@Lazy注解解决循环依赖
    @Autowired
    public CacheService(@Lazy VolcengineApiService volcengineApiService) {
        this.volcengineApiService = volcengineApiService;
    }

    @Cacheable(
            value = "imageAnalysisCache",
            key = "#imageHash + '-' + #finalPrompt",
            unless = "#result == null"
    )
    public String analyzeImageWithCache(String imageHash, String base64Image, String finalPrompt) throws Exception {
        log.info("缓存未命中，执行API调用（hash: {}, prompt: {}）",
                imageHash, finalPrompt.substring(0, Math.min(50, finalPrompt.length())) + "...");
        return volcengineApiService.callVolcengineApi(base64Image, finalPrompt);
    }
}