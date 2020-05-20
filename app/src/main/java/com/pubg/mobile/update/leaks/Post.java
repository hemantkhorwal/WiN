package com.pubg.mobile.update.leaks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class Post extends AppCompatActivity {
    FirebaseStorage storage;
    private StorageReference mStorageRef;
    WebView wb ;
    //TextView show_title;
    //ImageView abt_us;
    String url,title,type;
    File rootPath;
    Button ask_per_btn;
    CardView ask_per_alert;

    private static final String POST_TYPE = "POST";
    private static final String LINK_TYPE = "LINK";
    AdView mAdView;
    private InterstitialAd mInterstitialAd;

    ProgressBar loading;
    private static final int REQ_CODE = 1818;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Bundle e = getIntent().getExtras();
        if(e!=null){
            url = e.getString("URL");
            title = e.getString("TITLE");
            type = e.getString("TYPE");

        }

        if(type.equals(POST_TYPE)){

        mAdView = findViewById(R.id.adView);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd.loadAd(adRequest);

        loading = findViewById(R.id.loading);
        ask_per_alert = findViewById(R.id.ask_per_alert);
        ask_per_alert.setVisibility(View.GONE);
        ask_per_btn = findViewById(R.id.ask_per_btn);
        //show_title = findViewById(R.id.meta_title);
        //show_title.setText(title);
        //abt_us = findViewById(R.id.about_us);

        wb = findViewById(R.id.web_view);
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl(url);
        checkPermission();


       /* abt_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Post.this,AboutUs.class);
                startActivity(i);
            }
        });*/

        ask_per_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        }else if(type.equals(LINK_TYPE)){
            Intent open = new Intent(Intent.ACTION_VIEW);
            open.setData(Uri.parse(url));
            startActivity(open);
            finish();
        }
    }

    public void setUpWebPage(){
        //Glide.with(this).load("https://media.giphy.com/media/sB5M22pB6NSzS/giphy.gif").into(loading);
        changeProgressBarState(true);
        rootPath = new File(Environment.getExternalStorageDirectory(), "PUBG Updates");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,".post-id.html");

        //Adjsut file name in future cache

        mStorageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                       // Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();
                        changeProgressBarState(false);
                        wb.loadUrl("file:///"+localFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Try Later", Toast.LENGTH_SHORT).show();
                Log.d("DevLog",exception.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed(){

        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }

        super.onBackPressed();
        if(rootPath.exists()){
            for(File f : rootPath.listFiles()){
                f.delete();
            }
            rootPath.delete();
        }
        if(!isActivityRunning(MainActivity.class)){
            startActivity(new Intent(Post.this, MainActivity.class));
            finish();
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}  ,REQ_CODE);
        }else {
            setUpWebPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ask_per_alert.getVisibility()==View.VISIBLE){
                    ask_per_alert.setVisibility(View.GONE);
                }
                setUpWebPage();
            }else {
               ask_per_alert.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changeProgressBarState(boolean makeVisible){

        if(makeVisible){
            loading.setVisibility(View.VISIBLE);
        }else {
            loading.setVisibility(View.GONE);
        }
    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
           if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
}
