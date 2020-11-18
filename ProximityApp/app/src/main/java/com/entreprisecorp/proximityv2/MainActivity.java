package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.imagesManager.imagesConversion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView mail;
    private TextView password;
    private Button connectionButton;

    public static String uuidUser;
    public static String emailUser;

    private String mailText;
    private String passwordText;
    private SessionManager sessionManager;
    private HashMap<String,String> userDetails;

    private TextView textSignup;



    // Threshold for minimal keyboard height.
    //final int MIN_KEYBOARD_HEIGHT_PX = 150;
    // Top-level window decor view.
    //final View decorView = MainActivity.getWindow().getDecorView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());

        //----------link with view-----------------------------//

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        connectionButton = findViewById(R.id.buttonconnection);
        textSignup = findViewById(R.id.textviewSignup);
        //---------------------------------------------------------------//

        //----------Connection if already connected-----------------------------//
        if(sessionManager.isLoggin()){
            userDetails = sessionManager.getUserDetail();
            String mail = userDetails.get("email");
            String password = userDetails.get("password");
            Connection(mail,password);
        }

        //----------Listener Button----------------------------//
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText = password.getText().toString();
                mailText = mail.getText().toString().trim();

                Connection(mailText,passwordText);

            }
        });

        textSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });

    }

    /**
     * Recup uuid from server
     * @param email
     * @param password
     */
    public void Connection(String email, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/account/signIn";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mail", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("test", jsonBody.toString());
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            uuidUser =  response.getString("uuid");
                            sessionManager.Logout();

                            //-----------if good account---------//
                            if(!uuidUser.equals("0")){

                                SessionManager.age = response.getInt("age");
                                SessionManager.name = response.getString("name");
                                SessionManager.firstname = response.getString("firstname");
                                sessionManager.CreateSession(email, password);
                                SessionManager.uuid = uuidUser;
                                SignIn();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mail.setText("Error Json");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mail.setText("Error getting response");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }





    /**
     * Intent to homeScreen if logged in
     */
    private void SignIn() {
        startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
    }

    private void SignUp(){
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        finish();
    }

}