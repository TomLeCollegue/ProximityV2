package com.entreprisecorp.proximityv2;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.entreprisecorp.proximityv2.imagesManager.imagesConversion;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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

    private Context mContext;

    private Bitmap profileImageBitmap;
    private BitmapDrawable profileImageBitmapDraw;

    private String TAG = "test";
    private String filepath;

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

        mContext = getApplicationContext();


        netMain = new NetworkHelper(getApplicationContext(), sessionManager.getUserDetail().get("email"));

        name.setText(MainActivity.emailUser);

        filepath = mContext.getExternalFilesDir(null).getAbsolutePath();
        File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
        Log.d(TAG, dir.toString());
        File file = new File (dir, sessionManager.getUserDetail().get("email")+ "_pic.jpg");

        if (file.exists()){
            profileImageBitmapDraw = new BitmapDrawable(Resources.getSystem(), file.toString());
            displayProfileImage(profileImageBitmapDraw);
            Log.d(TAG, "it exists");
        } else {
            dir.mkdir();
            downloadProfileImage(sessionManager.getUserDetail().get("email"));
            displayProfileImage(profileImageBitmapDraw);
            Log.d(TAG, "it doesnt exists");
        }

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

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/images/" + email + "/download";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                /*profileImage.setImageBitmap(response);
                profileImage.setVisibility(View.VISIBLE);*/
                profileImageBitmap = response;
                profileImage.setImageBitmap(response);
                profileImageBitmapDraw = new BitmapDrawable(Resources.getSystem(),profileImageBitmap);
                saveToInternalStorage(profileImageBitmapDraw);

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

    private void saveToInternalStorage(BitmapDrawable bitmap_profile){
        OutputStream outputStream = null;

        Bitmap bitmap = new imagesConversion().getBitmapFromDrawable(bitmap_profile);

        String filepath = mContext.getExternalFilesDir(null).getAbsolutePath();
        File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
        File file = new File (dir, sessionManager.getUserDetail().get("email")+ "_pic.jpg");
        try{
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        new imagesConversion().compressImageToJpeg(bitmap,70,outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fos = openFileOutput("profil",Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // write object to file
            //oos.writeObject(MainActivity.profil);
            // closing resources
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayProfileImage(BitmapDrawable profileImageBitmapDraw){

        profileImage.setImageDrawable(profileImageBitmapDraw);

    }


    // ******** START AND STOP netWork Service ******** //
    public void startService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        stopService(serviceIntent);
    }

}