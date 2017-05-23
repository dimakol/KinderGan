package com.devsoul.dima.kindergarten.helper.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.util.Log;
import com.devsoul.dima.kindergarten.R;
import com.devsoul.dima.kindergarten.activities.MissingActivity;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Helper class of notification job
 */
public class ShowNotificationJob extends Job
{
    static final String TAG = "ShowNotificationJob";

    @NonNull
    @Override
    protected Result onRunJob(Params params)
    {
        Log.i(TAG, "onRunJob:" + params.getId());
        createNotification();
        schedulePeriodic();
        return Result.SUCCESS;
    }

    private void createNotification()
    {
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(getContext(), MissingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        Notification notification = new Notification.Builder(getContext())
                .setContentTitle("Missing child")
                .setContentText("Your child is absent today from kindergarten")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] { 1000, 3000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }

    /**
     * Schedule exact job on the time that was given
     * @param hour
     * @param minutes
     */
    public static void scheduleExact(int hour, int minutes)
    {
        long time = getRequiredTime(hour, minutes) - System.currentTimeMillis();

        new JobRequest.Builder(ShowNotificationJob.TAG)
                      .setExact(time)
                      .setPersisted(true)
                      .setUpdateCurrent(true)
                      .build()
                      .schedule();
    }

    /**
     * Schedule periodic job every 24 hours
     */
    public static void schedulePeriodic()
    {
        new JobRequest.Builder(ShowNotificationJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(1))
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    private static long getRequiredTime(int hour, int minutes)
    {
        Calendar now = Calendar.getInstance();
        Calendar required_time = Calendar.getInstance();

        required_time.set(Calendar.HOUR_OF_DAY, hour);
        required_time.set(Calendar.MINUTE, minutes);

        // Alarm in the same day
        if (now.getTimeInMillis() < required_time.getTimeInMillis())
        {
            return required_time.getTimeInMillis();
        }

        // Alarm in next day
        required_time.add(Calendar.DATE, 1);
        return required_time.getTimeInMillis();
    }
}
