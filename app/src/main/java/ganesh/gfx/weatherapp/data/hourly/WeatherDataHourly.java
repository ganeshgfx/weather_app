
package ganesh.gfx.weatherapp.data.hourly;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WeatherDataHourly {

    @SerializedName("cod")
    @Expose
    public String cod;
    @SerializedName("message")
    @Expose
    public Double message;
    @SerializedName("cnt")
    @Expose
    public Double cnt;
    @SerializedName("list")
    @Expose
    public java.util.List<ganesh.gfx.weatherapp.data.hourly.List> list = null;
    @SerializedName("city")
    @Expose
    public City city;

}
