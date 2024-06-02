package com.noorulaain.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeHome;
    private ProgressBar loadingPB;
    private TextView cityNames, temperatureTV, conditionTv;
    private TextInputEditText editCity;
    private RecyclerView weatherRV;
    private ImageView backIV, iconIV, searchIV;

    private ArrayList<WeatherRVModel> arrayList;
    private WeatherAdapter weatherAdapter;

    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        relativeHome = findViewById(R.id.relativeHome);
        loadingPB = findViewById(R.id.loading);
        cityNames = findViewById(R.id.TvcN);
        temperatureTV = findViewById(R.id.TVtemp);
        conditionTv = findViewById(R.id.TVcondition);
        editCity = findViewById(R.id.EditCity);
        weatherRV = findViewById(R.id.RVweather);
        backIV = findViewById(R.id.IVback);
        iconIV = findViewById(R.id.IVicon);
        searchIV = findViewById(R.id.SearchCity);

        arrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, arrayList);
        weatherRV.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            requestLocationUpdates();
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editCity.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city", Toast.LENGTH_SHORT).show();
                } else {
                    cityName = city;
                    cityNames.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "City Not Found ");
                        Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=d58ac98742854254abe163623242005&q="
                + cityName + "&days=1&aqi=yes&alerts=no";

        cityNames.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPB.setVisibility(View.GONE);
                        relativeHome.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        try {
                            JSONObject current = response.getJSONObject("current");
                            String temperature = current.getString("temp_c");
                            temperatureTV.setText(temperature + "°C");
                            int isDay = current.getInt("is_day");
                            String condition = current.getJSONObject("condition").getString("text");
                            String conditionIcon = current.getJSONObject("condition").getString("icon");
                            Picasso.get().load("https:" + conditionIcon).into(iconIV);
                            conditionTv.setText(condition);

                            if (isDay == 1) {
                                Picasso.get().load("https://images.unsplash.com/photo-1506765515384-028b60a970df").into(backIV);
                            } else {
                                Picasso.get().load("https://images.unsplash.com/photo-1503264116251-35a269479413").into(backIV);
                            }

                            JSONObject forecast = response.getJSONObject("forecast");
                            JSONArray forecastday = forecast.getJSONArray("forecastday");
                            JSONObject dayForecast = forecastday.getJSONObject(0);
                            JSONArray hourArray = dayForecast.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String temper = hourObj.getString("temp_c");
                                String img = hourObj.getJSONObject("condition").getString("icon");
                                String wind = hourObj.getString("wind_kph");
                                arrayList.add(new WeatherRVModel(time, temper, img, wind));
                            }

                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
                Log.e("API Error", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
