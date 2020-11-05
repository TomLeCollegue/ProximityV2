package com.entreprisecorp.proximityv2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_NEW_PERSON = "newPersonChannel";
    public static final String CHANNEL_ID_SERVICE = "serviceNetwork";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel newPerson = new NotificationChannel(
                    CHANNEL_NEW_PERSON,
                    "New Person nearby",
                    NotificationManager.IMPORTANCE_HIGH
            );
            newPerson.setDescription("Nouvelle personn notif");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(newPerson);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_SERVICE,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }

}
