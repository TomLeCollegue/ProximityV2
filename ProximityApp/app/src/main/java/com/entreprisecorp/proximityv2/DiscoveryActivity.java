package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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


        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            int id = (int) bundle.get("id_profil");

        }

    }
}