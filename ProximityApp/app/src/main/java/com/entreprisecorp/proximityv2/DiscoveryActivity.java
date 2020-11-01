package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;

import static android.graphics.Bitmap.Config.RGB_565;

public class DiscoveryActivity extends AppCompatActivity {


    private Person personDiscovered;
    private ImageView profilPic;

    private ImageView cross;
    private ImageView check;

    private TextView age;
    private TextView firstname;
    private TextView algo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        profilPic = findViewById(R.id.profile_image);
        cross = findViewById(R.id.cross);
        check = findViewById(R.id.check);
        age = findViewById(R.id.age);
        firstname = findViewById(R.id.name);
        algo = findViewById(R.id.algo);




        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            int id = (int) bundle.get("id_profil");
            personDiscovered = NotificationActivity.persons.get(id);
        }

        downloadProfileImage(personDiscovered.getEmail());
        age.setText(personDiscovered.getAge() + " ans");
        firstname.setText(personDiscovered.getFirstname());







    }


    // ***** Download and display Image Profile **** //
    public void downloadProfileImage(String email){

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + email + "/download";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profilPic.setImageBitmap(response);
                profilPic.setVisibility(View.VISIBLE);
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

}