
package ganesh.gfx.weatherapp.data.hourly;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WeatherList {

    @SerializedName("dt")
    @Expose
    public Long dt;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("weather")
    @Expose
    public java.util.List<Weather> weather = null;
    @SerializedName("clouds")
    @Expose
    public Clouds clouds;
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("visibility")
    @Expose
    public Integer visibility;
    @SerializedName("pop")
    @Expose
    public Double pop;
    @SerializedName("rain")
    @Expose
    public Rain rain;
    @SerializedName("sys")
    @Expose
    public Sys sys;
    @SerializedName("dt_txt")
    @Expose
    public String dtTxt;

}
