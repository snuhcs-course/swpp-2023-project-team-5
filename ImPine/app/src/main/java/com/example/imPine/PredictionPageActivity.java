package com.example.imPine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.imPine.model.WeatherResponse;
import com.example.imPine.network.RetrofitClient;
import com.example.imPine.network.WeatherService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Rain, Wind Speed, Humidity, Cloud
public class PredictionPageActivity extends AppCompatActivity {
    private EditText editRain;
    private EditText editTemp;
    private EditText editHumidity;
    private EditText editCloud;
    private EditText editWind;
    private double rainValue;
    private double temperatureValue;
    private int humidityValue;
    private double windValue;
    private int cloudValue;
    private String key = "ef868854e4da3eaea9540158413372dc";

    private void addTextWatchers() {
        editRain.addTextChangedListener(new GenericTextWatcher() {
            @Override
            void updateValue(String s) {
                rainValue = parseDouble(s);
            }
        });

        editTemp.addTextChangedListener(new GenericTextWatcher() {
            @Override
            void updateValue(String s) {
                temperatureValue = parseDouble(s);
            }
        });

        editHumidity.addTextChangedListener(new GenericTextWatcher() {
            @Override
            void updateValue(String s) {
                humidityValue = parseInt(s);
            }
        });

        editCloud.addTextChangedListener(new GenericTextWatcher() {
            @Override
            void updateValue(String s) {
                cloudValue = parseInt(s);
            }
        });

        editWind.addTextChangedListener(new GenericTextWatcher() {
            @Override
            void updateValue(String s) {
                windValue = parseDouble(s);
            }
        });
    }

    private abstract class GenericTextWatcher implements TextWatcher {
        abstract void updateValue(String s);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateValue(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setDefaultWeatherValues() {
        WeatherService service = RetrofitClient.getWeatherService();
        service.getCurrentWeather("Seoul", key).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    // This will run on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Extract and store the weather data
                                rainValue = weather.getRain() != null ? weather.getRain().getOneHour() : 0.0;
                                temperatureValue = weather.getMain() != null ? weather.getMain().getTemp() - 273.15 : 0.0; // Convert from Kelvin to Celsius
                                humidityValue = weather.getMain() != null ? weather.getMain().getHumidity() : 0;
                                windValue = weather.getWind() != null ? weather.getWind().getSpeed() : 0.0;
                                cloudValue = weather.getClouds() != null ? weather.getClouds().getAll() : 0;


                                // Set the default values in EditTexts
                                editRain.setText(String.format(Locale.getDefault(), "%.2f", rainValue));
                                editTemp.setText(String.format(Locale.getDefault(), "%.2f", temperatureValue));
                                editHumidity.setText(String.format(Locale.getDefault(), "%d", humidityValue));
                                editWind.setText(String.format(Locale.getDefault(), "%.2f", windValue));
                                editCloud.setText(String.format(Locale.getDefault(), "%d", cloudValue));

                                // Log the weather data
                                Log.d("weatherInfo", "Rain: " + rainValue + "mm");
                                Log.d("weatherInfo", "Temp: " + (temperatureValue) + "°C"); // Convert from Kelvin to Celsius
                                Log.d("weatherInfo", "Humidity: " + humidityValue + "%");
                                Log.d("weatherInfo", "Wind Speed: " + windValue + "km/h");
                                Log.d("weatherInfo", "Cloudiness: " + cloudValue + "%");
                            } catch (Exception e) {
                                Log.e("weatherInfo", "Exception in setting text: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d("weatherInfo", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("weatherInfo", "API call failed: " + t.getMessage());
            }
        });
    }



    private void setPineyImage(int avatarValue) {
        int drawableResourceId = getAvatarDrawableId(avatarValue);
        ImageView pineappleAvatar = findViewById(R.id.piney);

        Glide.with(this)
                .load(drawableResourceId)
                .into(pineappleAvatar);
    }
    private int getAvatarDrawableId(int avatarValue) {
        switch (avatarValue) {
            case 0: return R.drawable.pine_avatar;
            case 1: return R.drawable.twofatty;
            case 2: return R.drawable.threelazy;
            case 3: return R.drawable.fourbrowny;
            case 4: return R.drawable.fivecooly;
            case 5: return R.drawable.sixalien;
            case 6: return R.drawable.sevenalien;
            case 7: return R.drawable.eightavatar;
            case 8: return R.drawable.nineavatar;
            default: return R.drawable.pine_avatar;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setPineyImage(HomePageActivity.avatarFromHome);
        // Fetch and set weather data each time the activity resumes
        setDefaultWeatherValues();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_page);

        // Initialize the EditTexts
        editRain = findViewById(R.id.editRain);
        editTemp = findViewById(R.id.editTemperature);
        editHumidity = findViewById(R.id.editHumidity);
        editCloud = findViewById(R.id.editCloud);
        editWind = findViewById(R.id.editWind);
        addTextWatchers();

        ImageButton pineyButton = findViewById(R.id.piney);

        setDefaultWeatherValues();
        // Load the animations
        final Animation swayRight = AnimationUtils.loadAnimation(this, R.anim.sway_right);
        final Animation swayLeft = AnimationUtils.loadAnimation(this, R.anim.sway_left);

        // Set animation listeners to create an infinite swaying effect for piney
        swayRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        swayLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pineyButton.startAnimation(swayRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        pineyButton.startAnimation(swayRight);


        Button btnPredict = findViewById(R.id.btnPredict);
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare to send either user input or default values
                Intent intent = new Intent(PredictionPageActivity.this, PredictionResultActivity.class);
                intent.putExtra("temperatureValue", temperatureValue);
                intent.putExtra("rainValue", rainValue);
                intent.putExtra("humidityValue", humidityValue);
                intent.putExtra("cloudValue", cloudValue);
                intent.putExtra("windValue", windValue);

                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Diary button click
        ImageButton diaryButton = findViewById(R.id.diary);
        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DiaryPageActivity
                Intent intent = new Intent(PredictionPageActivity.this, DiaryPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Home button click
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // Friends button click
        ImageButton friendButton = findViewById(R.id.friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, FriendsPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        // logout button click
        ImageButton outButton = findViewById(R.id.logOut);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PredictionPageActivity.this, AuthLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus(); // Optional: Clear focus from the current EditText
                }
                return false;
            }
        });
    }


}
