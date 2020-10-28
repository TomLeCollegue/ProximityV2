package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView mail;
    private TextView password;
    private Button connectionButton;

    private String mailText;
    private String passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------link with view-----------------------------//
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        connectionButton = findViewById(R.id.buttonconnection);


        //----------Listener Button----------------------------//
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText = password.getText().toString();
                mailText = mail.getText().toString();

                postData();

            }
        });





    }

    public void postData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String URL = "http://192.168.43.36:8080/RestFullTEST-1.0-SNAPSHOT/account/signIn";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mail", mail.getText());
            jsonBody.put("password", password.getText());
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
                            mail.setText("String Response : "+ response.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
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

}