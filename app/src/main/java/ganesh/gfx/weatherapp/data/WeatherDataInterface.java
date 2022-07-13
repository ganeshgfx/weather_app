package ganesh.gfx.weatherapp.data;

import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.data.info.WeatherInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherDataInterface {

    @GET("weather?&units=metric&lang=en")
    Call<WeatherInfo> getData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String key
    );

    @GET("forecast?&units=metric&lang=en")

    Call<WeatherDataHourly> getHourlyData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String key
    );
}
