package ganesh.gfx.weatherapp.data.Location;

import android.location.Address;
import android.util.Log;

public class MLocation {
    Address address;
    LocationData locationData;

    String addressLine;
    double latitude,longitude;

    public MLocation(Address address) {
        this.address = address;

        addressLine = address.getAddressLine(0);
        latitude = address.getLatitude();
        longitude = address.getLongitude();
    }
    public MLocation(LocationData locationData) {
        this.locationData = locationData;

        addressLine = locationData.getAddress();
        latitude = locationData.getLat();
        longitude = locationData.getLon();
    }

    public String getAddressLine() {
        return addressLine;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void chexk(){
        if(locationData!=null){
            Log.d("TAG", "locationData");
        }else if(address!=null) {
            Log.d("TAG", "address");
        }//else return null;
    }
}
