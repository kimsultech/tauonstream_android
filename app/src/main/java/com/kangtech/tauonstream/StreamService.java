package com.kangtech.tauonstream;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.kangtech.tauonstream.util.Server;
import com.kangtech.tauonstream.util.SharedPreferencesUtils;

// not Used

public class StreamService extends Service {

    SimpleExoPlayer player;
    MediaItem mediaItem;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = new SimpleExoPlayer.Builder(this).build();
        // Build the media item.
        mediaItem = MediaItem.fromUri("http://" + Server.BASE_URL + ":" + SharedPreferencesUtils.getString("port", "7590") + "/stream.ogg");
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                    new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_round_play_arrow_24)
                    .setContentTitle("title")
                    .setContentText("Don't CLick Here")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setChannelId("313131")
                    .setContentIntent(pendingIntent)
                    .build();

            notification.defaults |= Notification.DEFAULT_SOUND;
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            // === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                String channelId = "313131";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }


            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, notification);

            startForeground(0, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        player.stop();
    }
}
