package com.projectocean.safar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation);
//                lottieAnimationView.cancelAnimation();
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                overridePendingTransition(R.anim.zoom_enter, 0);
                finish();
            }
        }, 5000);


    }
}
