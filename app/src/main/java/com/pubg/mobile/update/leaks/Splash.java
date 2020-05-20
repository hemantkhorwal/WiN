package com.pubg.mobile.update.leaks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().hasExtra("notification")) {
            Log.d("DevLog","Notification");
            String URL = getIntent().getExtras().getString("URL");
            String TITLE = getIntent().getExtras().getString("TITLE");

            Intent intent = new Intent(this, Post.class);


            intent.putExtra("URL",URL);
            intent.putExtra("TITLE",TITLE);
            intent.putExtra("TYPE","POST");
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                }
            }, 2000);
        }
    }

}
