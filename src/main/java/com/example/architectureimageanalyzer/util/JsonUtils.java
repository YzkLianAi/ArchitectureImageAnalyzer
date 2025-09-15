package com.example.architectureimageanalyzer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON序列化工具类（用于将对象转换为字符串）
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败: " + e.getMessage(), e);
        }
    }
}
