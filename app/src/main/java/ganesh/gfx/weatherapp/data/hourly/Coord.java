
package ganesh.gfx.weatherapp.data.hourly;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Coord {

    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("lon")
    @Expose
    public Double lon;

}
