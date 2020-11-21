package com.example.trakkus.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.trakkus.FriendNotificationActivity;
import com.example.trakkus.FriendsActivity;
import com.example.trakkus.R;

//notification

public class NotificationHelper extends ContextWrapper {

    private static final String Trakkus_CHANNEL_ID = "com.example.trakkus";
    private static final String Trakkus_CHANNEL_NAME = "trakkuschannel";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel trakkus = new NotificationChannel(Trakkus_CHANNEL_ID, Trakkus_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        trakkus.enableLights(true);
        trakkus.enableVibration(true);
        trakkus.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(trakkus);


    }


    public NotificationManager getManager() {

        if (manager == null) {

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }

        return manager;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getFnfLocationTrackerNotification(String title, String content, Uri defaultsound) {
        Intent intent = new Intent(this, FriendNotificationActivity.class);

        PendingIntent pendingIntent = TaskStackBuilder.create(getApplicationContext())
                .addNextIntent(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(getApplicationContext(), Trakkus_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultsound)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

    }
}
