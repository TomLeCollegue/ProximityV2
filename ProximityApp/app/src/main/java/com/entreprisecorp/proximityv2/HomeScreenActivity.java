package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.entreprisecorp.proximityv2.accounts.SessionManager;

public class HomeScreenActivity extends AppCompatActivity {

    private ImageView logout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        logout = findViewById(R.id.enveloppe);
        sessionManager = new SessionManager(getApplicationContext());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.Logout();
                startActivity(new Intent(HomeScreenActivity.this, MainActivity.class));
            }
        });

    }
}