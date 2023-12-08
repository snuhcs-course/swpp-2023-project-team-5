package com.imPine.imPineThankYou.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Base URL for your EC2 instance
    private static final String BASE_URL_EC2 = "http://ec2-13-209-72-91.ap-northeast-2.compute.amazonaws.com:8000";

    // Base URL for the OpenWeatherMap API
    private static final String BASE_URL_OPEN_WEATHER_MAP = "https://api.openweathermap.org/";

    // Separate Retrofit instances for each service
    private static Retrofit retrofitEC2 = null;
    private static Retrofit retrofitOpenWeatherMap = null;

    // Get a client for your EC2 instance
    public static Retrofit getClient() {
        if (retrofitEC2 == null) {
            retrofitEC2 = new Retrofit.Builder()
                    .baseUrl(BASE_URL_EC2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitEC2;
    }

    // Get a client for the OpenWeatherMap service
    public static WeatherService getWeatherService() {
        if (retrofitOpenWeatherMap == null) {
            retrofitOpenWeatherMap = new Retrofit.Builder()
                    .baseUrl(BASE_URL_OPEN_WEATHER_MAP) // Make sure to use the correct base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitOpenWeatherMap.create(WeatherService.class);
    }

    // Private constructor to prevent instantiation
    private RetrofitClient() {
        // Private constructor to prevent instantiation
    }

    public static String getBaseUrl() {
        return BASE_URL_EC2;
    }
}
