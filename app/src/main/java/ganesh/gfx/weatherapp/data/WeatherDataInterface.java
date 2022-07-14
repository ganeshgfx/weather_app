package ganesh.gfx.weatherapp.data;

import java.util.List;

import ganesh.gfx.weatherapp.data.Location.LocationData;
import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.data.info.WeatherInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherDataInterface {

    @GET("data/2.5/weather?&units=metric&lang=en")
    Call<WeatherInfo> getData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String key
    );

    @GET("data/2.5/forecast?&units=metric&lang=en")
    Call<WeatherDataHourly> getHourlyData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String key
    );

    @GET("geo/1.0/direct?")
    Call<List<LocationData>> getLocation(
            @Query("q") String q,
            @Query("limit") int limit,
            @Query("appid") String key
    );
}
