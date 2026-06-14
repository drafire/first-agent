package com.drafire.agent.weather.dummy;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherAgent {

    private final ReactAgent reactAgent;

    public WeatherAgent(@Value("${ali.api-key}") String apiKey) {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)   //必须把apiKey放在参数那里，而不是使用@Value的形式读取
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

        ToolCallback toolCallback = FunctionToolCallback.builder("getWeather", new WeatherTool()) //注意这里的name，最好不要有空格
                .description("get city weather")
                .inputType(WeatherRequest.class)  //这个类型对应WeatherRequest 里面的第一个参数
                .build();

        //创建Agent
        reactAgent = ReactAgent.builder()
                .name("weather agent")
                .model(chatModel)
                .tools(toolCallback)
                .systemPrompt("你是一个优秀的ai")
                .saver(new MemorySaver())
                .build();

    }

    public String checkWeather(String city) throws GraphRunnerException {

        AssistantMessage assistantMessage = reactAgent.call(city + "今天的天气怎么样？");
        return assistantMessage.getText();
    }
}
