package ganesh.gfx.weatherapp.ui.home;

import static ganesh.gfx.weatherapp.MainNavPage.wheatherDataChanged;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ganesh.gfx.weatherapp.BuildConfig;
import ganesh.gfx.weatherapp.MainNavPage;
import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.data.Location.LocationData;
import ganesh.gfx.weatherapp.data.Location.MLocation;
import ganesh.gfx.weatherapp.data.WeatherDataInterface;
import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.data.info.WeatherInfo;

import ganesh.gfx.weatherapp.databinding.FragmentHomeBinding;
import ganesh.gfx.weatherapp.ui.home.recycle.LocationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    View root;
    TextInputLayout textInputLayout;
    ExtendedFloatingActionButton fab;
    TextView textView;
    RecyclerView recyclerView;
    MaterialCardView location_layout;

    Gson gson;

    Retrofit retrofit;
    WeatherDataInterface apiService;

    boolean extend = true;

    final String API_KEY = BuildConfig.WHETHER_KEY;
    String URL = "https://api.openweathermap.org/";

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        textView = root.findViewById(R.id.output);
        textInputLayout = root.findViewById(R.id.location);
        recyclerView = root.findViewById(R.id.locationRe);
        location_layout = root.findViewById(R.id.inputUi);

        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String place = charSequence.toString().trim();
                if(!place.equals(""))
                    getLocation(charSequence.toString());
            }

            @Override public void afterTextChanged(Editable editable) { }
        });

        fab = root.findViewById(R.id.extended_fab);
        fab.setExtended(!extend);
        fab.setOnClickListener(view -> {
            String place = textInputLayout.getEditText().getText().toString().trim();
            if (!extend && !place.equals(""))
                getLocation(place);
            showInputLayout(extend);
            extend = !extend;
            fab.setExtended(!extend);
        });

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layout);

        gson = new GsonBuilder().setPrettyPrinting().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setPrettyPrinting().create()))
                .build();
        apiService = retrofit.create(WeatherDataInterface.class);

        loading(true);
        loadWeather();
        //getWeather(1.0,1);

        return root;
    }

    private void loadWeather() {
        if(MainNavPage.getLatitude()!=999 && MainNavPage.getLongitude()!=999 ) {
            getWeatherHourly(MainNavPage.getLatitude(), MainNavPage.getLongitude());
        }
        else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> loadWeather(), 1000);
        }
    }

    void showInputLayout(boolean extend) {
        if (extend) {
            textInputLayout.setVisibility(View.VISIBLE);
            location_layout.setVisibility(View.VISIBLE);
        } else {
            textInputLayout.setVisibility(View.GONE);
            location_layout.setVisibility(View.GONE);
        }
    }

    private void getWeather(double lat, double lon) {
        loading(true);
        try {
            Call<WeatherInfo> call = apiService.getData(lat, lon, API_KEY);
            call.enqueue(new Callback<WeatherInfo>() {
                @Override
                public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                    if (response.code() == 200) {
                        WeatherInfo weatherInfo = response.body();
                        textView.setText(textView.getText() + "\n" + gson.toJson(weatherInfo));
                        loading(false);
                    } else {
                        Toast toast = Toast.makeText(
                                getContext(),
                                " ⚠️ Error : " + response.message(),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        loading(false);
                    }
                }
                @Override
                public void onFailure(Call<WeatherInfo> call, Throwable t) {
                    Toast toast = Toast.makeText(getContext(), " ⚠️ Error ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    loading(false);
                }
            });
        } catch (Exception err) {
            Log.e("TAG", "onCreateView: " + err.getMessage(), err.fillInStackTrace());
        }
    }

    public void getWeatherHourly(double lat, double lon) {
        loading(true);
        showInputLayout(false);
        if(wheatherDataChanged){
            try {
                Call<WeatherDataHourly> call = apiService.getHourlyData(lat, lon, API_KEY);
                call.enqueue(new Callback<WeatherDataHourly>() {
                    @Override
                    public void onResponse(Call<WeatherDataHourly> call, Response<WeatherDataHourly> response) {
                        if (response.code() == 200) {
                            WeatherDataHourly data = response.body();
                            MainNavPage.weatherDataHourly = data;
                            wheatherDataChanged = false;
                            setInfoUi(data);
                            loading(false);

                        } else {
                            Toast toast = Toast.makeText(getContext(), " ⚠️ Error : " + response.message(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            loading(false);
                        }
                    }
                    @Override
                    public void onFailure(Call<WeatherDataHourly> call, Throwable t) {
                        Toast toast = Toast.makeText(getContext(), " ⚠️ Error "+t.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("TAG", "onFailure: " + t.getMessage(), t);
                        loading(false);
                    }
                });

            } catch (Exception err) {
                Toast.makeText(getContext(), "" + err.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onCreateView: " + err.getMessage(), err.fillInStackTrace());
            }
        }else {
            setInfoUi(MainNavPage.weatherDataHourly);
            loading(false);
        }
    }

    private void setInfoUi(WeatherDataHourly data) {

        //Toast.makeText(getContext(), ""+MainNavPage.selectedPage, Toast.LENGTH_SHORT).show();
        if(MainNavPage.selectedPage == R.id.navigation_home)
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.InfoFragment, new InfoFragment(data))
                    .commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void loading(boolean show) {
        CircularProgressIndicator circularProgressIndicator = root.findViewById(R.id.loading);
        if (show) {
            circularProgressIndicator.setVisibility(View.VISIBLE);
            return;
        }
        circularProgressIndicator.setVisibility(View.GONE);
    }

    LocationAdapter locationAdapter;
    public void getLocation(String place) {
        loading(true);
        textView.setText("");
        //useGeocoder(place);
        useOpenWeatherGeo(place);
    }
    void useOpenWeatherGeo(String place){
        Call<List<LocationData>> call = apiService.getLocation(place,10, API_KEY);
        call.enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse(Call<List<LocationData>> call, Response<List<LocationData>> response) {
                List<LocationData> locations = response.body();
                //Log.i("TAG", "onResponse: "+gson.toJson(locations));
                List<MLocation> mLocations = new ArrayList<>();
                for (LocationData l:locations){
                    mLocations.add(new MLocation(l));
                }
                makeRecylerForLocationList(mLocations);
            }

            @Override
            public void onFailure(Call<List<LocationData>> call, Throwable t) {
            }
        }) ;
    }
    @SuppressLint("StaticFieldLeak")
    private void useGeocoder(String place) {
        new AsyncTask<Void, Void, List<Address>>() {
            @Override
            protected List<Address> doInBackground(Void... voids) {
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocationName(place, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return address;
            }
            public void onPostExecute(List<Address> listAddresses) {

                if ((listAddresses != null) && (listAddresses.size() > 0)) {


                    List<MLocation> mLocations = new ArrayList<>();

                    for (Address address:listAddresses) {
                        mLocations.add(new MLocation(address));
                    }
//                    for (MLocation m:mLocations) {
//                        m.chexk();
//                    }

                    //Log.d("TAG", "onPostExecute: " + listAddresses.size());
                    makeRecylerForLocationList(mLocations);
                    loading(false);
                } else {
                    loading(false);
                }
            }
        }.execute();
    }

    private void makeRecylerForLocationList(List<MLocation> mLocations) {
        locationAdapter = new LocationAdapter(
                //listAddresses
                mLocations
        );
        recyclerView.setAdapter(
                locationAdapter
        );
        locationAdapter.setClickListener((view, address1) -> {
            wheatherDataChanged = true;
            getWeatherHourly(address1.getLatitude(), address1.getLongitude());
            fab.setExtended(false);
            extend = true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}