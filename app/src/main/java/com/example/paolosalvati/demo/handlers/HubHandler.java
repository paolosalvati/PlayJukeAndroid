package com.example.paolosalvati.demo.handlers;

/**
 * Created by Paolo on 13/02/2015.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.activities.MainActivity;

public class HubHandler extends  com.microsoft.windowsazure.notifications.NotificationsHandler  {


    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;


    //This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void updateMyActivity(Context context, String message) {
        Log.d("updateMyActivity","fffffffffhh");
        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }


    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        String a = bundle.getString("message").toString();
        Log.d("PAOLO Log Messagex", a);
        updateMyActivity(ctx,a);
        //sendNotification(nhMessage);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }




}
