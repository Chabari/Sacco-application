package com.george.mhadhirisaccoltd.Notifications;

import android.app.NotificationManager;
import android.support.v7.app.NotificationCompat;

import com.george.mhadhirisaccoltd.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by George on 3/15/2019.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_body=remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int mNotificationId=(int)System.currentTimeMillis();
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(mNotificationId,builder.build());

    }
}
