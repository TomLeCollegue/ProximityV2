package com.entreprisecorp.proximityv2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.HomeScreenActivity;
import com.entreprisecorp.proximityv2.MainActivity;
import com.entreprisecorp.proximityv2.R;
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


public class HomeScreenFragment extends Fragment {
    View View;

    private SessionManager sessionManager;
    private TextView name;
    private TextView age;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_home_screen, container, false);

        name = View.findViewById(R.id.name);
        age = View.findViewById(R.id.age);


        switchNetwork = View.findViewById(R.id.switchnetwork);
        profileImage = View.findViewById(R.id.profile_image);
        clientCo = View.findViewById(R.id.statesearch);
        sessionManager = new SessionManager(getContext());

        netMain = new NetworkHelper(getContext(), sessionManager.getUserDetail().get("email"));
        name.setText(MainActivity.emailUser);

        name.setText(SessionManager.firstname);
        age.setText(SessionManager.age + " ans");

        mContext = getContext();

        // ****** checks if the file "profilepicture" exists on your phone ******** //
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
        //**************************************************************************//




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


        return View;
    }

    // ***** Download and display Image Profile **** //
    public void downloadProfileImage(String email){

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/images/" + email + "/download";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                /*profileImage.setImageBitmap(response);
                profileImage.setVisibility(View.VISIBLE);*/
                profileImageBitmap = response;
                profileImage.setImageBitmap(response);
                profileImageBitmapDraw = new BitmapDrawable(Resources.getSystem(),profileImageBitmap);
                saveToInternalStorage(profileImageBitmapDraw, email);
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

    private void saveToInternalStorage(BitmapDrawable bitmap_profile, String email){
        OutputStream outputStream = null;

        Bitmap bitmap = new imagesConversion().getBitmapFromDrawable(bitmap_profile);

        String filepath = mContext.getExternalFilesDir(null).getAbsolutePath();
        File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
        File file = new File (dir, email+ "_pic.jpg");
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
            FileOutputStream fos = mContext.openFileOutput("profil", Context.MODE_PRIVATE);
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
        Intent serviceIntent = new Intent(getContext(), NetworkService.class);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(getContext(), NetworkService.class);
        getActivity().stopService(serviceIntent);
    }
}