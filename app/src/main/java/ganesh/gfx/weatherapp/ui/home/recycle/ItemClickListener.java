package ganesh.gfx.weatherapp.ui.home.recycle;

import android.location.Address;
import android.view.View;

import ganesh.gfx.weatherapp.data.Location.MLocation;

public interface ItemClickListener {
    public void onClick(View view, MLocation address);
}
