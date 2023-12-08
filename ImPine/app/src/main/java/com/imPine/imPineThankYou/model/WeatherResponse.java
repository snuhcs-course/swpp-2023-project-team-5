package com.imPine.imPineThankYou.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Rain rain; // This field might be null if there's no rain

    // ... other fields and getters/setters

    public Rain getRain() {
        return rain;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public static class Main {
        private double temp;
        private int humidity;
        // ... other fields like pressure, temp_min, temp_max

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }

        // ... other getters for the fields
    }

    public static class Wind {
        private double speed;
        private int deg;

        public double getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }
    }

    public static class Clouds {
        private int all;

        public int getAll() {
            return all;
        }
    }

    // The Rain class needs to handle cases when it's not present in the JSON.
    // It should be nullable or provide a default value.
    public static class Rain {
        @SerializedName("1h")
        private Double oneHour; // Use Double to allow null

        public Double getOneHour() {
            return oneHour != null ? oneHour : 0.0; // Return 0.0 if null
        }
    }
}
