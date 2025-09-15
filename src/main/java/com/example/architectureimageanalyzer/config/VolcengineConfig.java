package com.example.architectureimageanalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//火山引擎配置
@Component
@Data
@ConfigurationProperties(prefix = "volcengine.ark")
public class VolcengineConfig {
    private String region;
    private String apiKey;
    private String modelId;

    // Getters and Setters
/*    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }*/
}
