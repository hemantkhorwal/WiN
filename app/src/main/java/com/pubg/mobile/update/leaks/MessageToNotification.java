package com.pubg.mobile.update.leaks;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessageToNotification extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String URL = remoteMessage.getData().get("URL");
        String TITLE = remoteMessage.getData().get("TITLE");



        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification","true");
        intent.putExtra("URL",URL);
        intent.putExtra("TITLE",TITLE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        if(remoteMessage.getData()!=null)
            sendNotification(remoteMessage,pendingIntent);
    }


    private void sendNotification(RemoteMessage remoteMessage, PendingIntent pendingIntent) {

        Map<String,String> data = remoteMessage.getData();

        String title = data.get("title");
        String content = data.get("content");


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("MyNotifications");
            channel.enableLights(true);
            channel.setLightColor(Color.WHITE);
            channel.enableVibration(false);

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"MyNotifications");
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_about_us)
                .setTicker("tiker365")
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo("info")
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        notificationManager.notify(1,notificationBuilder.build());

    }
}
