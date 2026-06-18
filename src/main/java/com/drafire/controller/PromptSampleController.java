package com.drafire.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promptSample")
public class PromptSampleController {
    private final ChatClient chatClient;

    /**
     * 加载 System prompt tmpl.
     */
    @Value("classpath:/prompt/system-message.st")
    private Resource systemResource;

    /**
     * 我已经在ChatClientConfig中注入了ChatClient，所以这里不需要使用builder 那种方式进行注入了
     *
     * @param chatClient
     */
    public PromptSampleController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/roles")
    public Flux<String> generate(@RequestParam(
                                         value = "message",
                                         required = false,
                                         defaultValue = "Tell me about three famous pirates from the Golden Age of Piracy and why they did.  Write at least a sentence for each pirate.") String message,
                                 @RequestParam(value = "name", required = false, defaultValue = "Bob") String name,
                                 @RequestParam(value = "voice", required = false, defaultValue = "pirate") String voice) {
        // 用户输入
        UserMessage userMessage = new UserMessage(message);

        // 使用 System prompt tmpl
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

        //swagger ui是无法体现出逐字打印的效果的。
        return chatClient.prompt(new Prompt(List.of(userMessage, systemMessage)))
                .stream().content();
    }
}
