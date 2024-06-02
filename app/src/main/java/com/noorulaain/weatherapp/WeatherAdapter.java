package com.noorulaain.weatherapp;

import static android.view.Gravity.apply;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
   private Context context;
   private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    public WeatherAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }
    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherRVModel model= weatherRVModelArrayList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+ "°C");

//        String iconUrl = "http:" + model.getIcon();
//        Log.d("WeatherAdapter", "Icon URL: " + iconUrl);
//
//        Picasso.get().load(iconUrl).into(holder.conditionIV, new Callback() {
//            @Override
//            public void onSuccess() {
//                Log.d("WeatherAdapter", "Image loaded successfully");
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("WeatherAdapter", "Error loading image", e);
//            }
//        });


        Picasso.get().load("https:".concat(model.getIcon())).into(holder.conditionIV);
        holder.windSpeedTV.setText(model.getWindSpeed() +"km/h");
        SimpleDateFormat input= new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView temperatureTV,windSpeedTV , timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
       windSpeedTV=itemView.findViewById(R.id.TVwindSpeed);
       timeTV=itemView.findViewById(R.id.TVtime);
       temperatureTV=itemView.findViewById(R.id.TVtemp);
       conditionIV=itemView.findViewById(R.id.IVcondition);
        }
    }
}
