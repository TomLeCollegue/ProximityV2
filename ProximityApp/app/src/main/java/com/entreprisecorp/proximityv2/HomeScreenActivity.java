package com.entreprisecorp.proximityv2;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkService;

import static android.graphics.Bitmap.Config.RGB_565;

public class HomeScreenActivity extends AppCompatActivity {

    private ImageView user;
    private ImageView friendsIntent;
    private ImageView notifIntent;
    private SessionManager sessionManager;
    private TextView name;
    private TextView age;
    private TextView uuid;
    private TextView clientCo;
    public NetworkHelper netMain;
    private Switch switchNetwork;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        uuid = findViewById(R.id.uuid);
        user = findViewById(R.id.usericon);
        friendsIntent = findViewById(R.id.messages);
        notifIntent = findViewById(R.id.notificon);
        switchNetwork = findViewById(R.id.switchnetwork);
        profileImage = findViewById(R.id.profile_image);
        clientCo = findViewById(R.id.statesearch);
        sessionManager = new SessionManager(getApplicationContext());

        netMain = new NetworkHelper(getApplicationContext(), sessionManager.getUserDetail().get("email"));
        name.setText(MainActivity.emailUser);
        downloadProfileImage(sessionManager.getUserDetail().get("email"));

        name.setText(SessionManager.firstname);
        age.setText(SessionManager.age + " ans");
        uuid.setText(SessionManager.uuid);


        // ****** check if the network is running or not ******** //
        if(NetworkService.isInstanceCreated()){
            switchNetwork.setChecked(true);
            clientCo.setText("• Visible •");
            clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));
        }
        else{
            clientCo.setText("• Invisible •");
            clientCo.setTextColor(getResources().getColor(R.color.ColorRed));
        }

        switchNetwork.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked == true){
                clientCo.setText("• Visible •");
                clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));
                // **** Start the service ***** //
                startService();
            }
            else{
                clientCo.setText("• Invisible •");
                clientCo.setTextColor(getResources().getColor(R.color.ColorRed));
                // **** Stop the service ***** //
                stopService();
            }
        });

        //---------Listeners------------------------//
        user.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreenActivity.this, PointOfInterressedActivity.class));
        });

        friendsIntent.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreenActivity.this, FriendsListActivity.class));
        });

        notifIntent.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreenActivity.this, NotificationActivity.class));
        });





    }

    // ****** Gestion Permission ****** //
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart()
    {
        super.onStart();
        if (!hasPermissions(this, NetworkHelper.getRequiredPermissions())) {
            requestPermissions(NetworkHelper.getRequiredPermissions(), netMain.getRequestCodeRequiredPermissions());
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NetworkHelper.getRequestCodeRequiredPermissions()) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,"Permissions manquantes", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }


    // ***** Download and display Image Profile **** //
    public void downloadProfileImage(String email){

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + email + "/download";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profileImage.setImageBitmap(response);
                profileImage.setVisibility(View.VISIBLE);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDownload.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Error while downloading image", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(request);
    }

    // ******** START AND STOP netWork Service ******** //
    public void startService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        NetworkService.homeScreenActivity = this;
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        stopService(serviceIntent);
    }

}