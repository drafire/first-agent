package com.drafire.agent.weather.real;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.drafire.constant.Common;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherAgent {

    @Autowired
    private WeatherForLocationTool weatherForLocationTool;

    @Autowired
    private UerLocationTool uerLocationTool;

    private final ReactAgent reactAgent;

    public WeatherAgent(@Value("${ali.api-key}") String apiKey) {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(DashScopeChatModel.DEFAULT_MODEL_NAME)
                        .temperature(0.5)
                        .maxToken(1000)
                        .build())
                .build();

        this.reactAgent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .systemPrompt(Common.SYSTEM_PROMPT)
                //.tools(uerLocationTool.getUserLocationTool(), weatherForLocationTool.getWeatherTool())
                .saver(new MemorySaver())
                .build();
    }

    public String queryWeather(String city) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().addMetadata("user_id", "1")
                .build();
        AssistantMessage assistantMessage = this.reactAgent.call(city + "今天天气怎么样？", runnableConfig);
        System.out.println(assistantMessage.getText());
        return assistantMessage.getText();
    }
}
