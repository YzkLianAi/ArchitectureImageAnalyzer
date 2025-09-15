package com.example.architectureimageanalyzer.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * 配置图片分析结果的缓存策略
     *  - 过期时间：24小时（根据业务调整）
     *  - 最大容量：1000条（避免内存溢出）
     */
    @Bean
    public CacheManager imageAnalysisCacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)  // 写入后24小时过期
                .maximumSize(1000)  // 最大缓存条目数
                .recordStats();  // 记录缓存统计信息（可选）

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("imageAnalysisCache");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}