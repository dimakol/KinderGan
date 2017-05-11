package com.devsoul.dima.kindergarten.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.activities.MissingActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * This is the broadcast receiver for the push up notification of missing kid
 */
public class NotificationMessage extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent arg1)
    {
        showNotification(context);
    }

    private void showNotification(Context context)
    {
        Log.i("notification", "visible");

        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(context, MissingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Build notification
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Missing child")
                .setContentText("Your child is absent today from kindergarten")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);
    }
}
