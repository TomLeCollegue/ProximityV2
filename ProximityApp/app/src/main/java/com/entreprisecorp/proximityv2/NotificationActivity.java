package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.adapters.AdapterNotifications;
import com.entreprisecorp.proximityv2.adapters.AdapterProfilesFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements AdapterNotifications.OnItemClickListener {

    private ArrayList<Person> persons = new ArrayList<Person>();
    private RecyclerView rv;
    private AdapterNotifications MyAdapter;
    private ImageView homeIcon;
    private ImageView logout;
    private ImageView messages;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        GetNotif(SessionManager.uuid);
        Log.d("rv", "Friend done");
        rv = findViewById(R.id.recycler_view_friends);
        homeIcon= findViewById(R.id.homeicon);
        logout = findViewById(R.id.usericon);
        messages = findViewById(R.id.messages);
        sessionManager = new SessionManager(getApplicationContext());

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterNotifications(persons, this);
        rv.setAdapter(MyAdapter);
        MyAdapter.setonItemClickListener(NotificationActivity.this);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, HomeScreenActivity.class));
        });

        logout.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(NotificationActivity.this, MainActivity.class));
        });

        messages.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(NotificationActivity.this, FriendsListActivity.class));
        });




    }


    public void GetNotif(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/Friends/getDiscoveredByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("persons");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                String firstname = object.getString("firstname").trim();
                                String email = object.getString("email").trim();
                                int age = object.getInt("age");

                                Person person = new Person(name,firstname,age,email);

                                for (int j = 0; j < 20; j++) {

                                    persons.add(person);
                                }
                                MyAdapter.notifyDataSetChanged();
                            }
                            Log.d("friends", persons.toString() );
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

    public void onItemClick(int position) {
        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }
}