package ganesh.gfx.weatherapp;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class WeatherData {
    final String lat = ""+randomNumberGenerator();
    final String lon = ""+randomNumberGenerator();
    final String key = "e2534f623697d11918e8ae442c47c855";
    final String metric = "metric";
    final String lang = "en";

    Context context;

    WeatherData(Context context){
        this.context = context;

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + key + "&units=" + metric + "&lang="+lang;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                      //  Log.d("TAG", "onCreate: "+jsonObject.get("weather"));
                        Log.d("TAG", "onCreate: "+jsonObject.toString(4));
//                        textView.setText(
//                                jsonObject.toString(4)
//                        );
                    } catch (JSONException e) {
                        Log.e("TAG", e.getMessage(),e.fillInStackTrace() );

                    }
                },
                error -> {

                }
        );

        //jetpack bottom navigation

        //current weather

        //search location

        queue.add(stringRequest);

    }
    public static double randomNumberGenerator()
    {
        double rangeMin = -180.0f;
        double rangeMax = 180.0f;
        Random r = new Random();
        double createdRanNum = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return(createdRanNum);
    }

}
