package com.to_do.to_do_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper nh = new NotificationHelper(context);
        NotificationCompat.Builder nb = nh.getChannelNotification("Task is Due!", intent.getStringExtra("name"));
        nh.getManager().notify(intent.getIntExtra("notificationID", -1), nb.build());
    }
}
