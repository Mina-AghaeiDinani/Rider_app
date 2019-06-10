package com.example.deliveryman;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1;
    LocalBroadcastManager busAppRunning; //enables to send a broadcast message to all processes to know if an activity is running in foreground

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("DEVICE TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MSGRECEIVED", "remote: "+ remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String orderId = remoteMessage.getNotification().getTag(); //The ID of the order is stored in the tag when the notification is sent
            Map<String,String> data = remoteMessage.getData();
            String status = data.get("status");
            Log.d("STATUSSS", ""+status);
            /* We first send a message containing the order Id in broadcast to check if the desired activity is running*/
            //Intent broadcastIntent = new Intent("message_received");
            //broadcastIntent.putExtra("msg_order_id", orderId);
            //busAppRunning.getInstance(this).sendBroadcast(broadcastIntent);

            if (!status.equals("delivered")) {
                Log.d("NOTIFICATION", "Order: " + orderId);
                generateNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), orderId);
            }

        }


    }

    private void generateNotification(String body, String title, String orderId) {
        Intent intent = new Intent(this, OrderNotificationActivity.class);
        intent.putExtra("orderID", orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //FLAG_ACTIVITY_CLEAR_TOP

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent
                , PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0;
        }

        notificationManager.notify(NOTIFICATION_ID++,notificationBuilder.build());

    }


}

