// tanveer kaur
//shubhpreet singh
package com.example.trakkus.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.trakkus.Model.User;
import com.example.trakkus.R;
import com.example.trakkus.Utils.Commonx;
import com.example.trakkus.Utils.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMService extends FirebaseMessagingService {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationWithChannel(remoteMessage);

            else
                sendNotification(remoteMessage);

            addRequestToUserInformation(remoteMessage.getData());

        }


    }

    private void addRequestToUserInformation(Map<String, String> data) {

        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Commonx.USER_INFORMATION)
                .child(data.get(Commonx.TO_UID))
                .child(Commonx.FRIEND_REQUEST);

        User user = new User();
        user.setUid(data.get(Commonx.FROM_UID));
        user.setEmail(data.get(Commonx.FROM_NAME));

        acceptList.child(user.getUid()).setValue(user);

    }


    private void sendNotification(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String title = " Friend Requests";
        String content = "You have new friend request from " + data.get(Commonx.FROM_NAME);

        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultsound)
                .setAutoCancel(false);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(), builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificationWithChannel(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String title = "  Friend Request";
        String content = "You have new friend request from " + data.get(Commonx.FROM_NAME);


        NotificationHelper helper;
        Notification.Builder builder;

        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getFnfLocationTrackerNotification(title, content, defaultsound);

        helper.getManager().notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            final DatabaseReference tokens = FirebaseDatabase.getInstance()
                    .getReference(Commonx.TOKENS);
            tokens.child(user.getUid()).setValue(s);
        }
    }


}
