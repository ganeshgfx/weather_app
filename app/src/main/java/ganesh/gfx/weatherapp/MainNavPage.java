package ganesh.gfx.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.databinding.ActivityMainNavPageBinding;


public class MainNavPage extends AppCompatActivity {

    private static final String TAG = "TAG";
    private ActivityMainNavPageBinding binding;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    public static WeatherDataHourly weatherDataHourly;
    public static boolean wheatherDataChanged = true;
    public static int selectedPage;

    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainNavPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            Resources resources = this.getResources();
            int n = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        int dimensionPixelSizeNAV = resources.getDimensionPixelSize(n);




        //Log.d(TAG, "onCreate: nav Hight "+ dimensionPixelSize);
//        }

        selectedPage = R.id.navigation_home;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(2000);
            getCurrentLocation();
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocationForLatest();
                Log.d(TAG, "permission allowed");
            }else {
                locationPermissionRequest.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }
        navView = findViewById(R.id.nav_view);
        // FIXME: 7/14/22 refesh on ui change
        navView.setPadding(0,0,0,dimensionPixelSizeNAV);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_nav_page);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            selectedPage = destination.getId();
        });
        NavigationUI.setupWithNavController(binding.navView, navController);
        //getSupportActionBar().hide();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }
    static double latitude = 999;
    static double longitude = 999;
    public static double getLatitude() {
        return latitude;
    }
    public static double getLongitude() {
        return longitude;
    }
    private void getCurrentLocation() {
        if (true) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getCurrentLocation: PERMISSION_GRANTED");
                if (isGPSEnabled()) {
                    Log.d(TAG, "getCurrentLocation: getting location");
                    LocationServices.getFusedLocationProviderClient(getApplicationContext())
                            .requestLocationUpdates(locationRequest,
                                    new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            LocationServices.getFusedLocationProviderClient(MainNavPage.this)
                                                    .removeLocationUpdates(this);
                                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                                int index = locationResult.getLocations().size() - 1;
                                                latitude = locationResult.getLocations().get(index).getLatitude();
                                                longitude = locationResult.getLocations().get(index).getLongitude();
                                            }
                                        }
                                    }, Looper.getMainLooper());
                } else {
                    Log.d(TAG, "getCurrentLocation: GPS not enable");
                    turnOnGPS();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(getApplicationContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MainNavPage.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });
    }
    //for 12
    private void enableLoc() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                getLocationForLatest();
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(MainNavPage.this, 10);
                        } catch (IntentSender.SendIntentException e) {
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getCurrentLocation();
        } else {
            getLocationForLatest();
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    //for 12
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            getLocationForLatest();
                            Log.d(TAG, "Precise location access granted. ");
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            getLocationForLatest();
                            Log.d(TAG, "getCurrentLocation:  Only approximate location access granted.");
                        } else {
                            Log.d(TAG, ": No location access granted.");
                            Toast.makeText(this, "\uD83E\uDD7A Please allow location permission to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
    void getLocationForLatest() {
        if (
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Not allowed");
            return;
        }
        Log.d(TAG, "getting location");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d(TAG, latitude+" : "+longitude);
                    } else {
                        Log.d(TAG, "location NULL");
                        if(!isGPSEnabled() ){
                            Log.d(TAG, "getLocationForLatest: "+isGPSEnabled());
                            if(!gps){
                                gps = true;
                                enableLoc();
                            }
                        } else {
                            new Handler(Looper.getMainLooper()).postDelayed(() ->{
                                getLocationForLatest();
                            }, 2000);
                        }
                    }
                });
    }
    boolean gps = false;
}