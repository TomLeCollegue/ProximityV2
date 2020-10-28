package com.entreprisecorp.proximityv2;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;

public class HomeScreenActivity extends AppCompatActivity {

    private ImageView logout;
    private SessionManager sessionManager;
    private TextView name;
    public NetworkHelper netMain;
    private Switch switchNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        name = findViewById(R.id.name);
        logout = findViewById(R.id.enveloppe);
        switchNetwork = findViewById(R.id.switchnetwork);
        sessionManager = new SessionManager(getApplicationContext());

        netMain = new NetworkHelper(getApplicationContext(), MainActivity.emailUser);
        name.setText(MainActivity.uuidUser);
        logout.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(HomeScreenActivity.this, MainActivity.class));
        });

        switchNetwork.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked == true){
                netMain.SearchPeople();
            }
            else{
                netMain.StopAll();
            }
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
}