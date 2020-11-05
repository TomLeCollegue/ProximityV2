package com.entreprisecorp.proximityv2.nearbyconnection;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.entreprisecorp.proximityv2.HomeScreenActivity;
import com.entreprisecorp.proximityv2.MainActivity;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;

import java.util.HashMap;

import static com.entreprisecorp.proximityv2.App.CHANNEL_ID_SERVICE;

public class NetworkService extends Service {

    private NotificationManagerCompat notificationManager;
    private static NetworkService instance = null;
    public static HomeScreenActivity homeScreenActivity = null;
    public static NetworkHelper networkHelper;
    public SessionManager sessionManager;
    private String emailSession;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public static boolean ishomeScreenActivityCreated() {
        return homeScreenActivity != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        notificationManager = NotificationManagerCompat.from(this);
        sessionManager = new SessionManager(this);
        HashMap<String,String > user = sessionManager.getUserDetail();
        emailSession = user.get(sessionManager.EMAIL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
                .setContentTitle("Recherche Activée")
                .setContentText("Vous êtes Visible")
                .setSmallIcon(R.drawable.logo_proximity_round)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        networkHelper = new NetworkHelper(this, emailSession);
        networkHelper.SearchPeople();

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        networkHelper.StopAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
