package com.drafire.controller;

import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.drafire.agent.weather.dummy.DummyWeatherAgent;
import com.drafire.agent.weather.real.WeatherAgent;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    @Autowired
    private DummyWeatherAgent dummyWeatherAgent;

    @Autowired
    private WeatherAgent weatherAgent;

    @GetMapping("/query")
    public String streamChat(@RequestParam(value = "city", defaultValue = "查询天气") String city, HttpServletResponse response) throws GraphRunnerException {

        response.setCharacterEncoding("UTF-8");
        return dummyWeatherAgent.checkWeather(city);
    }


    @GetMapping("/v2/query")
    public String queryWeather(@RequestParam(value = "city", defaultValue = "查询天气") String city, HttpServletResponse response) throws GraphRunnerException {

        response.setCharacterEncoding("UTF-8");
        return weatherAgent.queryWeather(city);
    }
}
