package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
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

import org.json.JSONException;
import org.json.JSONObject;

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

    // Register global layout listener.
    /*decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout(){
                // Retrieve visible rectangle inside window.
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                // Decide whether keyboard is visible from changing decor view height.
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                        int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
                        // Notify listener about keyboard being shown.
                        listener.onKeyboardShown(currentKeyboardHeight);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        // Notify listener about keyboard being hidden.
                        listener.onKeyboardHidden();
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });*/

}