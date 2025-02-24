package com.example.weatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weatherapp.API.WeatherAPIClient;
import com.example.weatherapp.API.WeatherService;
import com.example.weatherapp.models.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etCity;
    private TextView tvResult;
    private Button btnCheck;
    private WeatherService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    void initView() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCity);
        tvResult = findViewById(R.id.tvResult);
        btnCheck = findViewById(R.id.btnCheck);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCheck.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim();
            if (!city.isEmpty()) {
                searchWeather(city);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a city name!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void initData() {
        weatherService = WeatherAPIClient.getInstance().create(WeatherService.class);

    }

    void searchWeather(String city) {
        String apiKey = "b622c061af7cafc011fa3cfaf554dc29";
        Call<WeatherResponse> call = weatherService.getWeather(city, apiKey, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayWeather(response.body());
                } else {
                    tvResult.setText("API Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                tvResult.setText("Network Error: " + t.getMessage());
            }
        });
    }

    void displayWeather(WeatherResponse weather) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        long sunriseTimestamp = (long) weather.getSys().getSunrise();
        long sunsetTimestamp = (long) weather.getSys().getSunset();

        if (sunriseTimestamp > 0) {
            String sunrise = sdf.format(new Date(sunriseTimestamp * 1000L));
        } else {
            String sunrise = "N/A";
        }

        if (sunsetTimestamp > 0) {
            String sunset = sdf.format(new Date(sunsetTimestamp * 1000L));
        } else {
            String sunset = "N/A";
        }

        String result = "City: " + weather.getName() + "\n" +
                "Temperature: " + weather.getMain().getTemp() + "°C\n" +
                "Humidity: " + weather.getMain().getHumidity() + "%\n" +
                "Pressure: " + weather.getMain().getPressure() + " hPa\n" +
                "Wind Speed: " + weather.getWind().getSpeed() + " m/s\n" +
                "Wind Direction: " + weather.getWind().getDeg() + "°\n" +
                "Sunrise: " + sunriseTimestamp + "\n" +
                "Sunset: " + sunsetTimestamp + "\n" +
                "Weather: " + weather.getWeathers().get(0).getDescription();

        tvResult.setText(result);
    }
}
