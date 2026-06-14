package com.drafire.agent.weather.dummy;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public class WeatherTool implements BiFunction<WeatherRequest, ToolContext, String> {
    @Override
    public String apply(WeatherRequest weatherRequest, ToolContext toolContext) {
        return "it's very rainy in in " + weatherRequest.getCity();
    }

    @NotNull
    @Override
    public <V> BiFunction<WeatherRequest, ToolContext, V> andThen(@NotNull Function<? super String, ? extends V> after) {
        return BiFunction.super.andThen(after);
    }
}
