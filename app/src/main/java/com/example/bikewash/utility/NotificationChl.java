package com.example.bikewash.utility;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
public class NotificationChl extends Application {

    public static final String CHANNEL_1 = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            android.app.NotificationChannel nc = new android.app.NotificationChannel( CHANNEL_1, CHANNEL_1, NotificationManager.IMPORTANCE_HIGH );
            NotificationManager nm = getSystemService( NotificationManager.class );
            nm.createNotificationChannel( nc );
        }
    }
}
