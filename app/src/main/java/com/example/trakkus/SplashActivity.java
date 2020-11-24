//tanveer kaur
package com.example.trakkus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trakkus.Utils.Commonx;

public class SplashActivity extends AppCompatActivity {
    //veriables
    public static int SPLASH_SCREEN = 3000;

    ImageView imageView;
    Animation topAnimation, bottonAnimation;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //hooks for animation
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottonAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        //image view
        imageView = findViewById(R.id.image_splash);
        imageView.setAnimation(topAnimation);
        //setHandler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (Commonx.loggedUser == null) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }


                finish();
            }
        }, SPLASH_SCREEN);
    }


}

