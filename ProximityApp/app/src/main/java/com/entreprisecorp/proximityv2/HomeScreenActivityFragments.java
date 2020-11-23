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
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.fragments.FriendsListFragment;
import com.entreprisecorp.proximityv2.fragments.HomeScreenFragment;
import com.entreprisecorp.proximityv2.fragments.NotificationFragments;
import com.entreprisecorp.proximityv2.fragments.UserFragment;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreenActivityFragments extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public NetworkHelper netMain;

    private BottomNavigationView bottomNavigationView;
    private TextView nameTab;
    private BadgeDrawable badgeNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_fragments);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        nameTab = findViewById(R.id.title_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        bottomNavigationView.setSelectedItemId(R.id.menuhomeicon);
        badgeNotif = bottomNavigationView.getOrCreateBadge(R.id.menunotificon);
        GetNotif(SessionManager.uuid);



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
                nameTab.setText("Profil");
                return true;
            case R.id.menumessageicon:
                setFragment(new FriendsListFragment());
                nameTab.setText("Amis");
                return true;
            case R.id.menuhomeicon:
                setFragment(new HomeScreenFragment());
                nameTab.setText("Proximity");
                return true;

            case R.id.menunotificon:
                setFragment(new NotificationFragments());
                nameTab.setText("Notifications");
                return true;

            case R.id.menusettingsIcon:
                setFragment(new HomeScreenFragment());
                nameTab.setText("Settings");
                return true;
        }
        return false;
    }

    public void GetNotif(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/Friends/getDiscoveredByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("persons");
                            if(jsonArray.length() > 0){
                                badgeNotif.setVisible(true);
                                badgeNotif.setNumber(jsonArray.length());
                            }
                            else{
                                badgeNotif.setVisible(false);
                            }
                        }
                        catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


}