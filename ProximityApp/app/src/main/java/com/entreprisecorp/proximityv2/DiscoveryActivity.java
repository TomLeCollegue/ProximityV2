package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.fragments.NotificationFragments;
import com.entreprisecorp.proximityv2.hobby.Hobby;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.graphics.Bitmap.Config.RGB_565;

public class DiscoveryActivity extends AppCompatActivity {


    private Person personDiscovered;
    private ImageView profilPic;

    private ImageView cross;
    private ImageView check;

    private Chip friend1;
    private Chip friend2;
    private Chip friend3;
    private Chip hobby1;
    private Chip hobby2;



    private TextView age;
    private TextView firstname;
    private SessionManager sessionManager;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        profilPic = findViewById(R.id.profile_image);
        cross = findViewById(R.id.cross);
        check = findViewById(R.id.check);
        age = findViewById(R.id.age);
        firstname = findViewById(R.id.name);

        friend1 = findViewById(R.id.friend1);
        friend2 = findViewById(R.id.friend2);
        friend3 = findViewById(R.id.friend3);

        hobby1 = findViewById(R.id.hobby1);
        hobby2 = findViewById(R.id.hobby2);


        sessionManager = new SessionManager(getApplicationContext());




        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            id = (int) bundle.get("id_profil");
            personDiscovered = NotificationFragments.persons.get(id);
        }

        downloadProfileImage(personDiscovered.getEmail());
        age.setText(personDiscovered.getAge() + " ans");
        firstname.setText(personDiscovered.getFirstname());

        GetHobbies(SessionManager.uuid);
        GetFriends(SessionManager.uuid);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefuseThePerson();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscoveryActivity.this, QuizzActivity.class);
                intent.putExtra("id_profil", id);
                startActivity(intent);

            }
        });





    }


    // ***** Download and display Image Profile **** //
    public void downloadProfileImage(String email){

        String urlDownload = "http://"+ SessionManager.IPSERVER + "/images/" + email + "/download";

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

    private void RefuseThePerson(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email2", personDiscovered.getEmail());
            jsonBody.put("email1", sessionManager.getUserDetail().get(SessionManager.EMAIL));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/nearby/RefusePerson";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String email = response.getString("response");
                            startActivity(new Intent(DiscoveryActivity.this, NotificationActivity.class));
                        }
                        catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void GetHobbies(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
            jsonBody.put("email", personDiscovered.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/similarity/GetHobbyInCommun";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hobbies");

                            if (jsonArray.length() >= 1){
                                JSONObject object = jsonArray.getJSONObject(0);
                                hobby1.setText(object.getString("name"));
                            }
                            else{
                                hobby1.setVisibility(View.GONE);
                            }
                            if (jsonArray.length() >= 2){
                                JSONObject object = jsonArray.getJSONObject(1);
                                hobby2.setText(object.getString("name"));
                            }
                            else{
                                hobby2.setVisibility(View.GONE);
                            }
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void GetFriends(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
            jsonBody.put("email", personDiscovered.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/similarity/GetFriendsInCommon";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("persons");

                            if (jsonArray.length() >= 1){
                                JSONObject object = jsonArray.getJSONObject(0);
                                friend1.setText(object.getString("firstname") + " " + object.getString("name"));
                            }
                            else{
                                friend1.setVisibility(View.GONE);
                            }
                            if (jsonArray.length() >= 2){
                                JSONObject object = jsonArray.getJSONObject(1);
                                friend2.setText(object.getString("firstname") + " " + object.getString("name"));
                            }
                            else{
                                friend2.setVisibility(View.GONE);
                            }
                            if (jsonArray.length() >= 3){
                                JSONObject object = jsonArray.getJSONObject(2);
                                friend3.setText("+" + (jsonArray.length() - 2));
                            }
                            else{
                                friend3.setVisibility(View.GONE);
                            }


                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                String firstname = object.getString("firstname").trim();
                                String email = object.getString("email").trim();
                                int age = object.getInt("age");

                                Person person = new Person(name,firstname,age,email);

                            }
                        }
                        catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }



}