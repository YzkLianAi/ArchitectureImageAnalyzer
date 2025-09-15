package com.example.architectureimageanalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "image")
@Data
public class ImageConfig {
    private long maxSize; // 最终存储：字节数
    private int maxWidth; // 像素数
    private int maxHeight; // 像素数

    // 处理max-size：将配置文件的"5"（单位：MB）转换为字节数
    public void setMaxSize(int maxSizeMB) {
        // 1MB = 1024 * 1024 字节
        this.maxSize = (long) maxSizeMB * 1024 * 1024;
    }

    // 注意：maxWidth和maxHeight的setter由lombok的@Data自动生成，直接接收int值
}
