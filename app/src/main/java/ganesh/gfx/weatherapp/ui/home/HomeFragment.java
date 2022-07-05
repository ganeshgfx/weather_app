package ganesh.gfx.weatherapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = root.findViewById(R.id.output);

        try {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setPrettyPrinting().create()))
                    .build();

            WeatherDataInterface apiService =
                    retrofit.create(WeatherDataInterface.class);

            Call<WeatherInfo> call = apiService.getData();
            call.enqueue(new Callback<WeatherInfo>() {

                @Override
                public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {

                    //Toast.makeText(getContext(), response.code()+"", Toast.LENGTH_SHORT).show();

                    if(response.code()==200) {
                        WeatherInfo weatherInfo = response.body();
                        Gson gson =  new GsonBuilder().setPrettyPrinting().create();
                        textView.setText(gson.toJson(weatherInfo));
                    }else {
                        Toast.makeText(getContext(), " ⚠️ Error", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<WeatherInfo> call, Throwable t) {

                }
            });

        }catch (Exception err){
            Log.e("TAG", "onCreateView: "+err.getMessage(),err.fillInStackTrace() );
        }


        textView.setText("dfsmiugu");
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}