package ganesh.gfx.weatherapp.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.data.hourly.WeatherDataHourly;
import ganesh.gfx.weatherapp.ui.home.recycle.WeatherAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoFragment() {
        // Required empty public constructor
    }

    WeatherDataHourly data;

    public InfoFragment(WeatherDataHourly data){
        this.data = data;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView recyclerView;
    WeatherAdapter adapter;
    TextView textView;

    ImageView mainDisp;

    Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        gson =  new GsonBuilder().setPrettyPrinting().create();

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        textView = view.findViewById(R.id.textView);
        recyclerView = (RecyclerView)view.findViewById(R.id.weather_list);
        mainDisp = view.findViewById(R.id.mainDisp);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(RecyclerView.HORIZONTAL);
//        layout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layout);

        adapter = new WeatherAdapter(
                data.list
        );
        recyclerView.setAdapter(
                adapter
        );

        Log.d("TAG", "===================================");
        Log.d("TAG", "\nonBindViewHolder: "+gson.toJson(data));

        //textView.setText(gson.toJson(data.city));
        textView.setText(Capitalize(data.list.get(0).weather.get(0).description));
        Glide.with(getContext()).load("https://openweathermap.org/img/wn/"+data.list.get(0).weather.get(0).icon+"@4x.png").into(mainDisp);

        Button location = view.findViewById(R.id.location);
        location.setText(data.city.name+", "+data.city.country);

        return view;
    }
    private String Capitalize(String i) {
        return i.substring(0, 1).toUpperCase() + i.substring(1).toLowerCase();
    }
}