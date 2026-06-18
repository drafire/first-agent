package com.drafire.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ChatClient dashScopeChatClient;

    public ImageController(ChatClient dashScopeChatClient) {
        this.dashScopeChatClient = dashScopeChatClient;
    }

    /**
     * 这个接口有问题。也许是因为百炼平台不能下载网络图片导致
     * @param prompt
     * @param imageUrl
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/analyze/url")
    public String analyzeImageByUrl(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt, @RequestParam String imageUrl) throws URISyntaxException {
        List<Media> uris = List.of(new Media(MimeTypeUtils.IMAGE_JPEG, new URI(imageUrl)));

        UserMessage message = UserMessage.builder().text(prompt).media(uris).build();
        // 设置消息格式为图片
        message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

        Prompt chatPrompt = new Prompt(message, DashScopeChatOptions.builder()
                .model("qwen-vl-max")  // 使用视觉模型
                .multiModel(true)  // 启用多模态
                .vlHighResolutionImages(true)  //启用高分辨率图片处理
                .temperature(0.7)
                .build());
        // 调用模型进行图片分析
        return dashScopeChatClient.prompt(chatPrompt).call().content();
    }

    /**
     * 图片分析接口 - 通过文件上传
     */
    @PostMapping("/image/analyze/upload")
    public String analyzeImageByUpload(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
                                       @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件类型
            if (!file.getContentType().startsWith("image/")) {
                return "请上传图片文件";
            }

            // 创建包含图片的用户消息
            Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource());
            UserMessage message = UserMessage.builder()
                    .text(prompt)
                    .media(media)
                    .build();

            // 设置消息格式为图片
            message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

            // 创建提示词，启用多模态模型
            Prompt chatPrompt = new Prompt(message,
                    DashScopeChatOptions.builder()
                            .withModel("qwen-vl-max")  // 使用视觉模型   qwen-vl-max-latest 这个模型，我的账号有问题的
                            .withMultiModel(true)             // 启用多模态
                            .withVlHighResolutionImages(true) // 启用高分辨率图片处理
                            .withTemperature(0.7)
                            .build());

            // 调用模型进行图片分析
            return dashScopeChatClient.prompt(chatPrompt).call().content();

        } catch (Exception e) {
            return "图片分析失败: " + e.getMessage();
        }
    }
}
