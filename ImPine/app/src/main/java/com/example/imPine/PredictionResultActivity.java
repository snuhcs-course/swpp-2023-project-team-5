package com.example.imPine;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imPine.network.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionResultActivity extends AppCompatActivity {
    public static double predictValueAPI(double T_mean, double Wind_speed, double Rain, double Humidity, double Cloud, double Pressure, double solar_radiation, double Soil_Moisture) {
        double intercept = -1.759527646;
        double[] coefficients = {-0.000152488, 0.004189188, 0.001467993, -0.105170651, 0.051506751, 0.001897868, 2.34361E-06, -0.190686857};
        double[] values = {T_mean, Wind_speed, Rain, Humidity, Cloud, Pressure, solar_radiation, Soil_Moisture};

        double prediction = intercept;
        for (int i = 0; i < coefficients.length; i++) {
            prediction += coefficients[i] * values[i];
        }

        return prediction;
    }
    public static double predict(double T_mean, double Wind_speed, double Rain, double Humidity, double Cloud) {
        // Coefficients from the regression output
        double intercept = 0.038898012;
        double coeffT_mean = 0.001091032;
        double coeffWind_speed = 0.004473261;
        double coeffRain = 0.001516761;
        double coeffHumidity = -0.047280111;
        double coeffCloud = 0.04867659;

        // Calculating the predicted value
        double predictedValue = intercept
                + (coeffT_mean * T_mean)
                + (coeffWind_speed * Wind_speed)
                + (coeffRain * Rain)
                + (coeffHumidity * Humidity)
                + (coeffCloud * Cloud);

        return predictedValue;
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

        String rainValue = getIntent().getStringExtra("rainValue");
        String temperatureValue = getIntent().getStringExtra("temperatureValue");
        String humidityValue = getIntent().getStringExtra("humidityValue");
        String cloudValue = getIntent().getStringExtra("cloudValue");
        String windValue = getIntent().getStringExtra("windValue");

        // Convert String values to float
        double rain = Double.parseDouble(rainValue);
        double temperature = Double.parseDouble(temperatureValue);
        double humidity = Double.parseDouble(humidityValue)/100;
        double cloud = Double.parseDouble(cloudValue)/100;
        double wind = Double.parseDouble(windValue);
        double fcr = predict(temperature, wind, rain, humidity, cloud) * 100;
        TextView textViewResult = findViewById(R.id.result);
        setBoldLabel(textViewResult, "Result: ", Double.toString(fcr) + "%");

//        // Create a new JSONObject and put the float values into it
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("Rain", rain);
//            jsonObject.put("T_mean", temperature);
//            jsonObject.put("Humidity", humidity);
//            jsonObject.put("Cloud", cloud);
//            jsonObject.put("Wind", wind);
//            // Add other parameters as needed
//        } catch (JSONException e) {
//            e.printStackTrace();  // Handle the exception
//        }
//
//        RequestBody body = RequestBody.create(jsonObject.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
//        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);
//
//        Call<ResponseBody> call = apiInterface.predictFcrStatus(body);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    // Handle response
//                    String fcrStatus = null;
//                    try {
//                        fcrStatus = response.body().string();
//                        TextView textViewResult = findViewById(R.id.result);
//                        textViewResult.setText(fcrStatus);
//                        // Parse fcrStatus as needed
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Handle failure
//            }
//        });

    }
}
