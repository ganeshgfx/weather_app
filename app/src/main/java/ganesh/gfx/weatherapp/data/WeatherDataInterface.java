package ganesh.gfx.weatherapp.data;

import java.util.List;

import ganesh.gfx.weatherapp.data.info.WeatherInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherDataInterface {
    @GET("weather?lat=0.1&lon=0.1&appid=e2534f623697d11918e8ae442c47c855&units=metric&lang=en")
    Call<WeatherInfo> getData();
}
