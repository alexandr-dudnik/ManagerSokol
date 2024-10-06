package com.sokolua.manager.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sokolua.manager.R;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.ui.activities.RootActivity;
import com.sokolua.manager.utils.AppConfig;
import com.sokolua.manager.utils.NetworkStatusChecker;

public class UpdateService extends Service {
    private Bitmap iconNotification = null;
    private Notification notification = null;
    private NotificationManager mNotificationManager = null;
    private int mNotificationId = 555777;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(AppConfig.SERVICE_ACTION_STOP) ){
            stopUpdateService();
            return START_STICKY_COMPATIBILITY;
        }

        generateForegroundNotification();

        runUpdate();

        return START_STICKY;
    }



    private void generateForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            final Intent intentStopService = new Intent(this, this.getClass());
//            intentStopService.setAction(AppConfig.SERVICE_ACTION_STOP);
//            final PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, intentStopService, 0);
//            final Bitmap cancelIconNotification = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cancel);
//
//            final Intent intentMainLanding = new Intent(this, RootActivity.class);
//            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0);
//            iconNotification = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_bird);
//            if (mNotificationManager == null) {
//                mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            }
//
//            assert(mNotificationManager != null);
//            mNotificationManager.createNotificationChannelGroup(
//                    new NotificationChannelGroup("sokol_manager", "updates")
//            );
//            final NotificationChannel notificationChannel =
//                    new NotificationChannel("updates_channel", "Update Service Notifications",
//                            NotificationManager.IMPORTANCE_MIN);
//            notificationChannel.enableLights(false);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            mNotificationManager.createNotificationChannel(notificationChannel);
//
//            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "updates_channel");
//
//            final Resources resources = getResources();
//            final String title = resources.getString(R.string.app_name) + " " + resources.getString(R.string.running_service);
//            builder.setContentTitle(title)
//                    .setTicker(title)
//                    .setContentText(resources.getString(R.string.service_touch_to_launch) + " " + resources.getString(R.string.app_name))
//                    .setSmallIcon(R.drawable.ic_logo_bird)
//                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .setWhen(0)
//                    .setOnlyAlertOnce(true)
//                    .setContentIntent(pendingIntent)
//                    .setOngoing(true)
//                    .addAction(R.drawable.ic_cancel, resources.getString(R.string.service_stop_update), pendingStopIntent);
//            if (iconNotification != null) {
//                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false));
//            }
//            builder.setColor(resources.getColor(R.color.color_red));
//            notification = builder.build();
//            startForeground(mNotificationId, notification);
        }

    }

    private void stopUpdateService(){
        stopForeground(true);
        stopSelf();
    }

    private void runUpdate(){
        final DataManager dataManager = DataManager.getInstance();

        if (!dataManager.getAutoSynchronize() || !NetworkStatusChecker.isNetworkAvailable()){
            stopUpdateService();
            return;
        }

        dataManager.updateAllAsync();

        while (dataManager.getJobManager().count() > 0){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
