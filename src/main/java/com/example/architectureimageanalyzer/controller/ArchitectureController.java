package com.example.architectureimageanalyzer.controller;

import com.example.architectureimageanalyzer.common.Result;
import com.example.architectureimageanalyzer.service.VolcengineApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/architecture")
public class ArchitectureController {

    // 注入API服务类
    private final VolcengineApiService volcengineApiService;

    // 构造函数注入
    public ArchitectureController(VolcengineApiService volcengineApiService) {
        this.volcengineApiService = volcengineApiService;
    }

    /**
     * 上传架构图片并获取分析结果（支持自定义提示词）
     */
    @PostMapping("/analyze")
    public Result<String> analyzeArchitecture(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "userPrompt", required = false, defaultValue = "") String userPrompt) {

        log.info("接收到架构图片上传请求，用户提示词长度: {}", userPrompt.length());

        try {
            String result = volcengineApiService.analyzeImage(imageFile, userPrompt);
            return Result.ok(result);
        } catch (IllegalArgumentException e) {
            return Result.fail("参数错误：" + e.getMessage());
        } catch (Exception e) {
            return Result.fail("服务器错误：" + e.getMessage());
        }
    }
}
