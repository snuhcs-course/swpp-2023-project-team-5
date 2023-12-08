package com.imPine.imPineThankYou.network;

import com.imPine.imPineThankYou.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather") // endpoint for OpenWeatherMap API
    Call<WeatherResponse> getCurrentWeather(@Query("q") String cityName, @Query("appid") String apiKey);
}