package com.example.imPine;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PredictionResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_result_page);

        // Retrieve the values passed from the previous activity
        String rainValue = getIntent().getStringExtra("rainValue");
        String windSpeedValue = getIntent().getStringExtra("windSpeedValue");
        String humidityValue = getIntent().getStringExtra("humidityValue");
        String cloudValue = getIntent().getStringExtra("cloudValue");

        // You can use these values for prediction using your model
        // For example:
        String predictionResult = predictUsingModel(rainValue, windSpeedValue, humidityValue, cloudValue);

        // Display the prediction result
        TextView textViewResult = findViewById(R.id.result);
        textViewResult.setText(predictionResult);
    }

    // This is just a placeholder method. Replace with actual model prediction logic.
    private String predictUsingModel(String rain, String windSpeed, String humidity, String cloud) {
        // Use your model for prediction and return the result
        return "Sample prediction based on inputs";  // This is just a placeholder return value
    }
}
