package com.drafire.controller;

import com.drafire.service.BaiLianEmbeddingService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/rag/basic")
public class RagBasicController {

    private final BaiLianEmbeddingService baiLianEmbeddingService;

    public RagBasicController(BaiLianEmbeddingService baiLianEmbeddingService) {
        this.baiLianEmbeddingService = baiLianEmbeddingService;
    }

    @PostMapping("upload")
    public String upload(@NotNull("file can't not be null") @RequestParam("file") MultipartFile file) throws IOException {
        // 1. 创建临时文件
        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());

        // 2. 将 MultipartFile 转存到临时文件中
        file.transferTo(tempFile);

        // 3. 包装为 FileSystemResource
        FileSystemResource fileSystemResource = new FileSystemResource(tempFile);
        baiLianEmbeddingService.embedDocument(fileSystemResource);
        return "success";
    }
}
