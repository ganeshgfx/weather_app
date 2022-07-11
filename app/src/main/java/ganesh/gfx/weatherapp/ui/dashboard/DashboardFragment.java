package ganesh.gfx.weatherapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ganesh.gfx.weatherapp.MainActivity;
import ganesh.gfx.weatherapp.MainNavPage;
import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.databinding.FragmentDashboardBinding;


public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textView;
        ImageView imageView;
        MaterialCardView cardView;

        textView = root.findViewById(R.id.text_home);
        imageView = root.findViewById(R.id.profile);
        cardView = root.findViewById(R.id.profileCard);

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FirebaseAuth.getInstance().signOut();
                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
                return false;
            }
        });

       //imageView.setImageBitmap(user.getPhotoUrl());
        Glide
                .with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_hourglass)
                .into(imageView);
        textView.setText(user.getDisplayName());
        setText(R.id.text_email,user.getEmail());
        setText(R.id.text_other,user.getUid());

//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void setText(int id,String text){
        TextView textView = root.findViewById(id);
        textView.setText(text);
    }

}