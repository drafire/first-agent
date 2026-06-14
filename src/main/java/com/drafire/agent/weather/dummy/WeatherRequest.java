package com.drafire.agent.weather.dummy;

public class WeatherRequest {
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public WeatherRequest(String city) {
        this.city = city;
    }
}
