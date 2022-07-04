package ganesh.gfx.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        findViewById(R.id.button).setOnClickListener(click->{
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}