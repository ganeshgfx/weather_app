package ganesh.gfx.weatherapp.ui.home.recycle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.data.hourly.Weather;
import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.RecycleViewHolder> {

    List<ganesh.gfx.weatherapp.data.hourly.List> list;
    Gson gson;
    public WeatherAdapter(List<ganesh.gfx.weatherapp.data.hourly.List> list){
        this.list = list;
        gson =  new GsonBuilder().setPrettyPrinting().create();
    }

    Context context;

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.weather_item_layout, parent, false);
        RecycleViewHolder viewHolder = new RecycleViewHolder(listItem);

        context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {

        ganesh.gfx.weatherapp.data.hourly.List info = list.get(position);

        //String string = gson.toJson(list.get(position).weather);
        //string += "\n+"+gson.toJson(list.get(position).dtTxt);
        String i =  info.weather.get(0).description;
        holder.info.setText(Capitalize(i));
        holder.temp.setText(info.main.temp+"°C");
//        holder.time.setText(info.dtTxt);

        Date date = new Date(info.dt*1000L);
        SimpleDateFormat jdf = new SimpleDateFormat(" hh:mm a, dd/MMMM");
        String java_date = jdf.format(date);

        holder.time.setText(java_date);

        Glide.with(context).load("https://openweathermap.org/img/wn/"+info.weather.get(0).icon+"@4x.png").placeholder(R.drawable.ic_hourglass).into(holder.ico);
    }

    @NonNull
    private String Capitalize(String i) {
        return i.substring(0, 1).toUpperCase() + i.substring(1).toLowerCase();
    }

    @Override
    public int getItemCount() {
       return list.size();
    }

    class RecycleViewHolder extends RecyclerView.ViewHolder{
        public TextView info;
        public TextView time;
        public TextView temp;
        public ImageView ico;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.info);
            time = itemView.findViewById(R.id.time);
            temp = itemView.findViewById(R.id.temp);
            ico = itemView.findViewById(R.id.wedIcon);
        }
    }
}
