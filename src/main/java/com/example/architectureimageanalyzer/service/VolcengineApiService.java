package com.example.architectureimageanalyzer.service;

import com.alibaba.fastjson.JSON;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class VolcengineApiService {

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
     * 处理图片并调用火山引擎API获取分析结果（支持自定义提示词）
     */
    public String analyzeImage(MultipartFile imageFile, String userPrompt) throws Exception {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("请上传有效的图片文件");
        }

        // 组合提示词
        String finalPrompt = combinePrompts(BASE_PROMPT, userPrompt);

        String base64Image = convertToBase64(imageFile);
        return callVolcengineApi(base64Image, finalPrompt);
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
     * 调用火山引擎API（带Token限制）
     */
    private String callVolcengineApi(String base64Image, String prompt) throws Exception {
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

            // 设置合理的最大Token限制（火山引擎视觉模型通常支持4096 tokens）
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(modelId)
                    .messages(messages)
                    .temperature(0.3)
                    .maxTokens(4096) // 设置为合理范围内的最大值
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