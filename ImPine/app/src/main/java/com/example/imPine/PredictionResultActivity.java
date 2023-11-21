package com.example.imPine;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_result_page);

        String rainValue = getIntent().getStringExtra("rainValue");
        String temperatureValue = getIntent().getStringExtra("temperatureValue");
        String humidityValue = getIntent().getStringExtra("humidityValue");
        String cloudValue = getIntent().getStringExtra("cloudValue");

        // Convert String values to float
        float rain = Float.parseFloat(rainValue);
        float temperature = Float.parseFloat(temperatureValue);
        float humidity = Float.parseFloat(humidityValue);
        float cloud = Float.parseFloat(cloudValue);

        // Create a new JSONObject and put the float values into it
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Rain", rain);
            jsonObject.put("T_mean", temperature);
            jsonObject.put("Humidity", humidity);
            jsonObject.put("Cloud", cloud);
            // Add other parameters as needed
        } catch (JSONException e) {
            e.printStackTrace();  // Handle the exception
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        ApiInterface apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.predictFcrStatus(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle response
                    String fcrStatus = null;
                    try {
                        fcrStatus = response.body().string();
                        TextView textViewResult = findViewById(R.id.result);
                        textViewResult.setText(fcrStatus);
                        // Parse fcrStatus as needed
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });

    }
}
