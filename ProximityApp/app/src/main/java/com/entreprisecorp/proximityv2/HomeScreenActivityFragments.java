package com.entreprisecorp.proximityv2;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.entreprisecorp.proximityv2.fragments.FriendsListFragment;
import com.entreprisecorp.proximityv2.fragments.HomeScreenFragment;
import com.entreprisecorp.proximityv2.fragments.UserFragment;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivityFragments extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public NetworkHelper netMain;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_fragments);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        bottomNavigationView.setSelectedItemId(R.id.menuhomeicon);
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


    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment,fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuusericon:
                setFragment(new UserFragment());
                return true;
            case R.id.menumessageicon:
                setFragment(new FriendsListFragment());
                return true;
            case R.id.menuhomeicon:
                setFragment(new HomeScreenFragment());
                return true;

            case R.id.menunotificon:
                setFragment(new HomeScreenFragment());
                return true;

            case R.id.menusettingsIcon:
                setFragment(new HomeScreenFragment());
                return true;
        }
        return false;
    }
}