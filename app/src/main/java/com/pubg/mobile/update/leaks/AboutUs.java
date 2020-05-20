package com.pubg.mobile.update.leaks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class AboutUs extends AppCompatActivity {

    RelativeLayout share ,rate ,privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        share = findViewById(R.id.share_us);
        rate = findViewById(R.id.rate_us);
        privacy = findViewById(R.id.privacy);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT,"Share News Pan");
                share.putExtra(Intent.EXTRA_TEXT,"Please share this great app called NewsPan");
                startActivity(Intent.createChooser(share,"Share On"));
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "https://www.readgoodbegood.com";
                Intent rate = new Intent(Intent.ACTION_VIEW);
                rate.setData(Uri.parse(URL));
                startActivity(rate);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "https://www.readgoodbegood.com";
                Intent privacy = new Intent(Intent.ACTION_VIEW);
                privacy.setData(Uri.parse(URL));
                startActivity(privacy);
                /*Intent openPost = new Intent(AboutUs.this,Post.class);
                openPost.putExtra("URL","https://www.readgoodbegood.com");
                openPost.putExtra("TITLE","Privacy Polciy");
                openPost.putExtra("TYPE","LINK");
                startActivity(openPost);*/
            }
        });
    }
}
