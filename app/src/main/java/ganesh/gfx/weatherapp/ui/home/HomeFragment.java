package ganesh.gfx.weatherapp.ui.home;


import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.data.WeatherDataInterface;
import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.data.info.WeatherInfo;


import ganesh.gfx.weatherapp.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    TextInputLayout textInputLayout;
ExtendedFloatingActionButton fab;
    TextView textView;

    Gson gson;

    View root;

    Retrofit retrofit;
    WeatherDataInterface apiService;

    boolean extend = true;

    final String API_KEY = "e2534f623697d11918e8ae442c47c855";
    String URL = "https://api.openweathermap.org/data/2.5/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        textView = root.findViewById(R.id.output);
        textInputLayout = root.findViewById(R.id.location);

        gson =  new GsonBuilder().setPrettyPrinting().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setPrettyPrinting().create()))
                .build();
         apiService =
                retrofit.create(WeatherDataInterface.class);


       //getWeather(0.14,0.17);
        getWeatherHourly(0.14,0.17);


        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(textInputLayout.getEditText().getText().toString());
            }
        });

        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        fab=root.findViewById(R.id.extended_fab);
        fab.setExtended(!extend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(extend){
                    textInputLayout.setVisibility(View.VISIBLE);
                }else {
                    textInputLayout.setVisibility(View.GONE);
                    getLocation(textInputLayout.getEditText().getText().toString());
                }
                extend = !extend;
                fab.setExtended(!extend);
            }
        });

        return root;
    }

    private void getWeather(double lat, double lon) {
        loading(true);
        try {
            Call<WeatherInfo> call = apiService.getData(lat,lon,API_KEY);
            call.enqueue(new Callback<WeatherInfo>() {

                @Override
                public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {

                    //Toast.makeText(getContext(), response.code()+"", Toast.LENGTH_SHORT).show();

                    if(response.code()==200) {
                        WeatherInfo weatherInfo = response.body();
                        //oast.makeText(getContext(), ""+gson.toJson(weatherInfo), Toast.LENGTH_SHORT).show();
                        textView.setText(textView.getText()+"\n"+gson.toJson(weatherInfo));
                        loading(false);
                    }else {
                        Toast toast = Toast.makeText(getContext(), " ⚠️ Error : "+response.message(), Toast.LENGTH_SHORT);
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

        }catch (Exception err){
            Log.e("TAG", "onCreateView: "+err.getMessage(),err.fillInStackTrace() );
        }
    }

    private void getWeatherHourly(double lat, double lon) {
        loading(true);
        try {
            Call<WeatherDataHourly> call = apiService.getHourlyData(lat,lon,API_KEY);
            call.enqueue(new Callback<WeatherDataHourly>() {
                @Override
                public void onResponse(Call<WeatherDataHourly> call, Response<WeatherDataHourly> response) {
                    if(response.code()==200) {
                        WeatherDataHourly weatherInfo = response.body();
                        //oast.makeText(getContext(), ""+gson.toJson(weatherInfo), Toast.LENGTH_SHORT).show();
                        //textView.setText(textView.getText()+"\n"+gson.toJson(weatherInfo.list));
                        //textView.setText(gson.toJson(weatherInfo.city));
                        //Log.d("TAG", "onResponse: "+weatherInfo.list.size());
                        setInfoUi(weatherInfo);
                        loading(false);
                    }else {
                        Toast toast = Toast.makeText(getContext(), " ⚠️ Error : "+response.message(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        loading(false);
                    }
                }

                @Override
                public void onFailure(Call<WeatherDataHourly> call, Throwable t) {

                    Toast toast = Toast.makeText(getContext(), " ⚠️ Error ", Toast.LENGTH_SHORT);

                    toast.show();
                    Log.e("TAG", "onFailure: "+t.getMessage(),t);
                    loading(false);
                }
            });

        }catch (Exception err){
            Toast.makeText(getContext(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onCreateView: "+err.getMessage(),err.fillInStackTrace() );
        }
    }

    private void setInfoUi(WeatherDataHourly data){

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.InfoFragment,new InfoFragment(data))
                .commit();
//        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left,
//                        R.anim.slide_in_from_left, R.anim.slide_out_to_right)
//                .replace(R.id.fragmentContainerView, myFragment)
//                .commit()
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void loading(boolean show){
        CircularProgressIndicator circularProgressIndicator = root.findViewById(R.id.loading);
        if(show){
            circularProgressIndicator.setVisibility(View.VISIBLE);
            return;
        }
        circularProgressIndicator.setVisibility(View.GONE);
    }

    //Location
    public void getLocation(String place) {
        loading(true);
        textView.setText("");

        new AsyncTask<Void, Void, List<Address>>() {
            @Override
            protected List<Address> doInBackground(Void... voids) {

                Geocoder geocoder  = new Geocoder(getContext());
                List<Address> address = null;
                try {
                   address = geocoder.getFromLocationName(place,1);
                } catch (IOException e) {
                    loading(false);
                    e.printStackTrace();
                }

                return address;
            }

            public void onPostExecute(List<Address> listAddresses) {
                Address address = null;
                if ((listAddresses != null) && (listAddresses.size() > 0)) {
                    address = listAddresses.get(0);
//                    getWeather(address.getLatitude(),address.getLongitude());
                    getWeatherHourly(address.getLatitude(),address.getLongitude());
                }
                else {
                    loading(false);
                    Toast.makeText(getContext(), "No Location Found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    //

}