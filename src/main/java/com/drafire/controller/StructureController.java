package com.drafire.controller;

import com.drafire.entity.BookEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这个controller用于练习返回结构化的结果
 */
@RestController
@RequestMapping("/structure")
public class StructureController {
    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final BeanOutputConverter<BookEntity> converter;
    private final String format;

    private static final Logger log = LoggerFactory.getLogger(StructureController.class);

    public StructureController(ChatClient chatClient, ChatModel chatModel) {
        this.chatClient = chatClient;
        this.chatModel = chatModel;
        this.converter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });
        this.format = converter.getFormat();
        log.info("format: {}", format);
    }

    @GetMapping("/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "以影子为作者，写一篇200字左右的有关人工智能诗篇") String query) {
        String result = chatModel.call(query);
        System.out.println("result:" + result);
        assert result != null;

        try {
            BookEntity bookEntity = converter.convert(result);
           log.info("反序列化成功，结果：" + bookEntity);
        } catch (Exception e) {
            log.info("反序列化失败");
        }

        return result;
    }
}
