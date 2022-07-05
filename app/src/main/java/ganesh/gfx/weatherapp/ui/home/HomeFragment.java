package ganesh.gfx.weatherapp.ui.home;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.data.WeatherDataInterface;
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

    TextView textView;

    Gson gson;

    Retrofit retrofit;
    WeatherDataInterface apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        textView = root.findViewById(R.id.output);
        textInputLayout = root.findViewById(R.id.location);

        gson =  new GsonBuilder().setPrettyPrinting().create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setPrettyPrinting().create()))
                .build();
         apiService =
                retrofit.create(WeatherDataInterface.class);


       // getWeather(0.14,0.17);


        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getloction(textInputLayout.getEditText().getText().toString());
            }
        });

        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void getWeather(double lat, double lon) {
        try {
            Call<WeatherInfo> call = apiService.getData(lat,lon);
            call.enqueue(new Callback<WeatherInfo>() {

                @Override
                public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {

                    //Toast.makeText(getContext(), response.code()+"", Toast.LENGTH_SHORT).show();

                    if(response.code()==200) {
                        WeatherInfo weatherInfo = response.body();
                        //oast.makeText(getContext(), ""+gson.toJson(weatherInfo), Toast.LENGTH_SHORT).show();
                        textView.setText(textView.getText()+"\n"+gson.toJson(weatherInfo));
                    }else {
                        Toast.makeText(getContext(), " ⚠️ Error : "+response.message(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<WeatherInfo> call, Throwable t) {

                }
            });

        }catch (Exception err){
            Log.e("TAG", "onCreateView: "+err.getMessage(),err.fillInStackTrace() );
        }
    }

    private void getloction(String place) {
        textView.setText("");

        Geocoder geocoder  = new Geocoder(getContext());
        try {
            List<Address> address = geocoder.getFromLocationName(place,1);
            if(address!=null){

//                addressList.forEach(address ->{
//                    textView.setText(textView.getText()+"\n\n"+address.getAddressLine(0)+"\n("+address.getLatitude()+":"+address.getLongitude()+")");
//                    getWeather(address.getLatitude(),address.getLongitude());
//                });

                //textView.setText(textView.getText()+"\n\n"+address.get(0).getAddressLine(0)+"\n("+address.get(0).getLatitude()+":"+address.get(0).getLongitude()+")");
                getWeather(address.get(0).getLatitude(),address.get(0).getLongitude());


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}