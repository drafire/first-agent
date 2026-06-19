package com.drafire.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeApiConfig {
    /**
     * 百炼调用时需要配置 DashScope API，对 dashScopeApi 强依赖。
     * @return
     */
    @Value("${ali.api-key}")
    private String apiKey;
    @Bean
    public DashScopeApi dashScopeApi() {

        return DashScopeApi.builder().apiKey(apiKey).build();
    }
}
