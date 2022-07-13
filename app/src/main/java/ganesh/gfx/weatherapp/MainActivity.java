package ganesh.gfx.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity{

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button signInButton;
    ImageView sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.signin);

        login();
        sun = findViewById(R.id.sun);
        int speed = 10000;
        int translation = 400;
        sun.startAnimation(getRotateAnimation(speed+1000));
        getCloud(R.id.clouds1)
                .startAnimation(getCloudAnimation(-translation, translation, 0, 100, speed));
        getCloud(R.id.clouds2)
                .startAnimation(getCloudAnimation(translation, -translation, 100, 0, speed));


        signInButton.setOnClickListener(click->{
            signIn();
        });
        signInButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //FirebaseAuth.getInstance().signOut();
                //Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    @NonNull
    private RotateAnimation getRotateAnimation(int speed) {
        RotateAnimation rotate = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                .50f,
                Animation.RELATIVE_TO_SELF,
                .50f);
        rotate.setDuration(speed);
        rotate.setRepeatCount(Animation.INFINITE);
        //rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new LinearInterpolator());
        return rotate;
    }
    @NonNull
    private Animation getCloudAnimation(int fromXDelta, int toXDelta, int fromYDelta, int toYDelta, int speed) {
        Animation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(speed);
        animation.setInterpolator(new AnticipateInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setFillAfter(true);
        return animation;
    }

    private void signIn() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken(BuildConfig.FIRE_KEY)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,2000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show();
                login();
                try {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    if(googleSignInAccount!=null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                        // Check credential
                        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                                .addOnCompleteListener(this, task1 -> {
                                    // Check condition
                                    if(task1.isSuccessful()) {
                                        // When task is successful
                                        Toast.makeText(MainActivity.this, "Authentication Done", Toast.LENGTH_SHORT).show();
                                        login();
                                    } else {
                                        // When task is unsuccessful
                                        // Display Toast
                                        Toast.makeText(MainActivity.this, "Authentication Failed :"+ task1.getException()
                                            .getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Log.e("TAG", "onActivityResult: "+e.getMessage(),e.fillInStackTrace() );
                }
            }
        }
    }
    private void login() {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, MainNavPage.class));
            finish();
        }
    }
    ImageView getCloud(int id){
        return (ImageView) findViewById(id);
    }
}