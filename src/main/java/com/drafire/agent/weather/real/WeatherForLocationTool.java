package com.drafire.agent.weather.real;

import com.drafire.agent.weather.WeatherRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class WeatherForLocationTool implements BiFunction<WeatherRequest, ToolContext, String> {
    @Override
    public String apply(@ToolParam(description = "city") WeatherRequest weatherRequest, ToolContext toolContext) {
        return "It's always sunny in " + weatherRequest.getCity();
    }

    @NotNull
    @Override
    public <V> BiFunction<WeatherRequest, ToolContext, V> andThen(@NotNull Function<? super String, ? extends V> after) {
        return BiFunction.super.andThen(after);
    }

    ToolCallback getWeatherTool(){
        return FunctionToolCallback.builder("getWeatherLocation",new WeatherForLocationTool())
                .description("get weather for a given city")
                .inputType(WeatherRequest.class)
                .build();
    }
}


