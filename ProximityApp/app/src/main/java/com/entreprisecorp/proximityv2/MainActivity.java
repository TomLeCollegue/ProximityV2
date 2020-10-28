package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView mail;
    private TextView password;
    private Button connectionButton;

    public static String uuidUser;

    private String mailText;
    private String passwordText;
    private SessionManager sessionManager;
    private HashMap<String,String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());

        //----------link with view-----------------------------//

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        connectionButton = findViewById(R.id.buttonconnection);

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
                mailText = mail.getText().toString();

                Connection(mailText,passwordText);

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

        String URL = "http://192.168.43.36:8080/RestFullTEST-1.0-SNAPSHOT/account/signIn";

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
                            uuidUser =  response.getString("id");
                            sessionManager.CreateSession(mailText, passwordText);
                            SignIn();
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

}