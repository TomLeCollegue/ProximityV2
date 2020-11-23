package com.entreprisecorp.proximityv2.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkHelper;
import com.entreprisecorp.proximityv2.nearbyconnection.NetworkService;

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
        downloadProfileImage(sessionManager.getUserDetail().get("email"));

        name.setText(SessionManager.firstname);
        age.setText(SessionManager.age + " ans");


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

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + email + "/download";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
        Intent serviceIntent = new Intent(getContext(), NetworkService.class);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(getContext(), NetworkService.class);
        getActivity().stopService(serviceIntent);
    }
}