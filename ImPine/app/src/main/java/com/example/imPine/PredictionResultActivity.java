package com.example.imPine;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imPine.model.DiseaseResponse;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionResultActivity extends AppCompatActivity {
    private double fcr;
    private RelativeLayout loadingPanel;

    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }

    private SpannableString getFcrPreventionTips(double fcrPercentage, double temperature, double rain, int humidity, int cloud, double wind) {
        SpannableStringBuilder tips = new SpannableStringBuilder();

        // General tips
        appendBoldText(tips, "General FCR Prevention Tips:\n");
        tips.append("- Ensure proper drainage to avoid waterlogging.\n");
        tips.append("- Use disease-free planting material.\n");
        tips.append("- Practice crop rotation to reduce pathogen buildup.\n\n");

        // Tips based on FCR percentage
        if (fcrPercentage > 15) {
            appendBoldText(tips, "High FCR Risk Tips:\n");
            tips.append("- Increase monitoring for early signs of disease.\n");
            tips.append("- Consider fungicide applications as a preventive measure.\n\n");
        } else if (fcrPercentage > 5) {
            appendBoldText(tips, "Moderate FCR Risk Tips:\n");
            tips.append("- Regularly inspect plants for early symptoms.\n");
            tips.append("- Improve air circulation around plants.\n\n");
        } else {
            appendBoldText(tips, "Low FCR Risk Tips:\n");
            tips.append("- Maintain healthy soil conditions.\n");
            tips.append("- Avoid injuring the plants as wounds can be infection sites.\n\n");
        }

        // Weather-specific tips
        appendBoldText(tips, "Weather-Based Tips:\n");

        // Temperature-specific tips
        final double highTemperatureThreshold = 25;
        final double lowTemperatureThreshold = 5;
        if (temperature > highTemperatureThreshold) {
            tips.append("- Provide shade to protect plants from excessive heat.\n");
            tips.append("- Ensure adequate watering to prevent heat stress.\n\n");
        } else if (temperature < lowTemperatureThreshold) {
            tips.append("- Have to keep soil warm! Consider using mulch.\n");
            tips.append("- Consider greenhouse cultivation to maintain optimal temperature.\n\n");
        }

        if (rain > 5) {
            tips.append("- Protect plants from heavy rains if possible.\n\n");
        }
        if (humidity > 80) {
            tips.append("- Reduce humidity around plants through ventilation.\n\n");
        }
        if (cloud > 60) {
            tips.append("- Ensure adequate sunlight exposure.\n\n");
        }
        if (wind > 20) {
            tips.append("- Shield plants from strong winds to prevent physical damage.\n\n");
        }

        return new SpannableString(tips);
    }
    private void appendBoldText(SpannableStringBuilder builder, String text) {
        int start = builder.length();
        builder.append(text);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, start + text.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_result_page);
        loadingPanel = findViewById(R.id.loadingPanel);
        showProgressBar();
        ImageView backButton = findViewById(R.id.backButton); // Get the "Back" button by ID

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PredictionResultActivity.this, PredictionPageActivity.class);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        double temperature = intent.getDoubleExtra("temperatureValue", 0.0);
        double wind = intent.getDoubleExtra("windValue", 0.0);
        double rain = intent.getDoubleExtra("rainValue", 0.0);
        int humidity = intent.getIntExtra("humidityValue", 0);
        int cloud = intent.getIntExtra("cloudValue", 0);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("T_mean", temperature);
            jsonObject.put("Wind_speed", wind);
            jsonObject.put("Rain", rain);
            jsonObject.put("Humidity", humidity);
            jsonObject.put("Cloud", cloud);
            Log.d("PredictionResultActivityWW", "JSON for API call: " + jsonObject.toString());
        } catch (JSONException e) {
            Log.e("PredictionResultActivityWW", "JSON Exception: ", e);
        }

        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        String authToken = AuthLoginActivity.getAuthToken(this);
        Call<DiseaseResponse> call = apiService.getFCR("Bearer: " + authToken, temperature, wind, rain, humidity, cloud);

        call.enqueue(new Callback<DiseaseResponse>() {
            @Override
            public void onResponse(Call<DiseaseResponse> call, Response<DiseaseResponse> response) {
                if (response.isSuccessful()) {
                    hideProgressBar();
                    fcr = response.body().getResult() * 100;
                    Log.d("PredictionResultActivityWW", "API Response: " + fcr);

                    TextView textViewResult = findViewById(R.id.result);
                    String formattedFcr = String.format(Locale.getDefault(), "%.2f%%", fcr);
                    setBoldLabel(textViewResult, "Result: ", formattedFcr);

                    SpannableString fcrTips = getFcrPreventionTips(fcr, temperature, rain, humidity, cloud, wind);
                    TextView textViewTips = findViewById(R.id.tip);
                    textViewTips.setText(fcrTips);

                } else {
                    hideProgressBar();
                    Log.e("PredictionResultActivityWW", "API call not successful. Response code: " + response.code());
                    Toast.makeText(PredictionResultActivity.this, "Failed to get prediction", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PredictionResultActivity.this, PredictionPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DiseaseResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("PredictionResultActivityWW", "API call failed: ", t);
                Toast.makeText(PredictionResultActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PredictionResultActivity.this, PredictionPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
