package com.example.architectureimageanalyzer.service;

import com.alibaba.fastjson.JSON;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class VolcengineApiService {
    private final CacheService cacheService;

    // 使用构造器注入和@Lazy注解解决循环依赖
    @Autowired
    public VolcengineApiService(@Lazy CacheService cacheService) {
        this.cacheService = cacheService;
    }

    // 其他代码保持不变...
    @Value("${volcengine.ark.api-key}")
    private String apiKey;

    @Value("${volcengine.ark.region}")
    private String region;

    @Value("${volcengine.ark.model-id}")
    private String modelId;

    // 基础提示词模板
    private static final String BASE_PROMPT =
            "你是一个软件架构分析专家。请仔细分析用户提供的架构图，专注于以下核心任务：\n\n" +
                    "1. 节点识别：准确识别图中的所有功能模块、组件或服务节点\n" +
                    "2. 关系描述：清晰描述节点之间的数据流向、调用关系或依赖关系\n" +
                    "3. 边界界定：明确各节点或分组的职责边界和交互接口\n\n" +
                    "输出要求：\n" +
                    "- 使用简洁明确的描述性语言\n" +
                    "- 避免主观评价或建议性内容\n" +
                    "- 专注于客观描述图中的实际内容\n" +
                    "- 确保术语使用一致，无歧义\n" +
                    "- 不要使用任何Markdown格式（包括标题符号、加粗、列表符号等），只需输出纯文本\n" +
                    "- 总描述长度控制在300-500字之间";

    /**
     * 处理图片并调用缓存服务获取分析结果
     */
    public String analyzeImage(MultipartFile imageFile, String userPrompt) throws Exception {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("请上传有效的图片文件");
        }

        // 生成图片唯一标识（MD5）
        String imageHash = generateImageHash(imageFile);
        log.info("图片哈希值: {}", imageHash);
        // 转换图片为Base64
        String base64Image = convertToBase64(imageFile);
        // 组合提示词
        String finalPrompt = combinePrompts(BASE_PROMPT, userPrompt);
        // 调用带缓存的分析方法
        return cacheService.analyzeImageWithCache(imageHash, base64Image, finalPrompt);
    }

    // 其他方法保持不变...

    /**
     * 生成图片文件的MD5哈希值（作为唯一标识）
     */
    private String generateImageHash(MultipartFile imageFile) throws Exception {
        byte[] imageBytes = imageFile.getBytes();
        return DigestUtils.md5DigestAsHex(imageBytes);
    }

    /**
     * 组合基础提示词和用户提示词
     */
    private String combinePrompts(String basePrompt, String userPrompt) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return basePrompt;
        }
        return basePrompt + "\n\n用户额外要求: " + userPrompt;
    }

    /**
     * 将图片转换为Base64编码
     */
    private String convertToBase64(MultipartFile imageFile) throws Exception {
        byte[] imageBytes = imageFile.getBytes();
        String fileExt = getFileExtension(imageFile.getOriginalFilename());
        return "data:image/" + fileExt + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 实现接口方法：调用火山引擎API
     */
    public String callVolcengineApi(String base64Image, String prompt) throws Exception {
        log.info("开始调用火山引擎API进行图片分析");
        ArkService arkService = ArkService.builder()
                .apiKey(apiKey)
                .region(region)
                .build();

        try {
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage userMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(buildMessageContent(base64Image, prompt))
                    .build();
            messages.add(userMessage);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(modelId)
                    .messages(messages)
                    .temperature(0.2)
                    .maxTokens(4096)
                    .build();

            return (String) arkService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } finally {
            arkService.shutdownExecutor();
        }
    }

    /**
     * 构建多模态消息内容
     */
    private String buildMessageContent(String base64Image, String prompt) {
        List<Object> contentList = new ArrayList<>();
        contentList.add(new ChatContent("text", prompt));
        contentList.add(new ChatContent("image_url", new ImageUrl(base64Image)));
        return JSON.toJSONString(contentList);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "png";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    // 内部辅助类
    private static class ChatContent {
        private String type;
        private Object text;

        public ChatContent(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public ChatContent(String type, ImageUrl imageUrl) {
            this.type = type;
            this.text = imageUrl;
        }

        public String getType() { return type; }
        public Object getText() { return text; }
    }

    // 内部辅助类
    private static class ImageUrl {
        private String url;

        public ImageUrl(String url) {
            this.url = url;
        }

        public String getUrl() { return url; }
    }
}