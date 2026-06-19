package com.drafire.service;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeCloudStore;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeStoreOptions;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaiLianEmbeddingService {
    private static final Logger logger = LoggerFactory.getLogger(BaiLianEmbeddingService.class);

    private static final String indexName = "微服务";

    @Value("classpath:/data/spring_ai_alibaba_quickstart.pdf")
    private Resource springAiResource;

    private static final String retrievalSystemTemplate = """
            Context information is below.
            ---------------------
            {question_answer_context}
            ---------------------
            Given the context and provided history information and not prior knowledge,
            reply to the user comment. If the answer is not in the context, inform
            the user that you can't answer the question.
            """;

    private final ChatClient chatClient;

    private final DashScopeApi dashscopeApi;

    /**
     * 加载 System prompt tmpl.
     */
    @Value("classpath:/prompt/system-message.st")
    private Resource systemResource;

    public BaiLianEmbeddingService(ChatClient.Builder builder, DashScopeApi dashscopeApi) {
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashscopeApi, DashScopeDocumentRetrieverOptions.builder()
                .indexName(indexName)
                .build());
        this.chatClient = builder
                .defaultAdvisors(new DocumentRetrievalAdvisor(documentRetriever, new SystemPromptTemplate(retrievalSystemTemplate)))
                .build();

        this.dashscopeApi = dashscopeApi;
    }

    public void embedDocument(Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.get();
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(512, 100, 5, 128, true);
        //文档分块
        List<Document> chunks = tokenTextSplitter.apply(documents);

        // 1. add documents to DashScope cloud storage
        VectorStore vectorStore = new DashScopeCloudStore(dashscopeApi, new DashScopeStoreOptions(indexName));
        vectorStore.add(chunks);

        logger.info("insert done");
    }

}
