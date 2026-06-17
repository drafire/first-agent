package com.drafire.agent.weather.real;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.drafire.agent.weather.WeatherRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.alibaba.cloud.ai.graph.agent.tools.ToolContextConstants.AGENT_CONFIG_CONTEXT_KEY;

@Service
public class UerLocationTool implements BiFunction<WeatherRequest, ToolContext, String> {

    @Override
    public String apply(@ToolParam(description = "city") WeatherRequest weatherRequest, ToolContext toolContext) {
        String userId = "";
        if (null != toolContext && null != toolContext.getContext()) {
            RunnableConfig runnableConfig = (RunnableConfig) toolContext.getContext().get(AGENT_CONFIG_CONTEXT_KEY);
            Optional<Object> userIdOptional = runnableConfig.metadata("user_id");
            if (userIdOptional.isPresent()) {
                userId = userIdOptional.get().toString();
            }

            if (null == userId) {
                userId = "1";
            }
        }
        return "1".equals(userId) ? "佛罗里达" : "上海";
    }

    @NotNull
    @Override
    public <V> BiFunction<WeatherRequest, ToolContext, V> andThen(@NotNull Function<? super String, ? extends V> after) {
        return BiFunction.super.andThen(after);
    }

    ToolCallback getUserLocationTool(){
        return FunctionToolCallback.builder("getWeather",new UerLocationTool())
                .description("receive user location base on user Id")
                .inputType(WeatherRequest.class)
                .build();
    }
}