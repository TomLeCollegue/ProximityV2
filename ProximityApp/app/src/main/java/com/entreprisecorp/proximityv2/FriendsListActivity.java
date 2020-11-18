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
import com.entreprisecorp.proximityv2.adapters.AdapterProfilesFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity implements AdapterProfilesFriends.OnItemClickListener{

    public static ArrayList<Person> friends = new ArrayList<Person>();
    private RecyclerView rv;
    private AdapterProfilesFriends MyAdapter;
    private ImageView homeIcon;
    private ImageView logout;
    private ImageView notificon;

    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        GetFriends(SessionManager.uuid);
        Log.d("rv", "Friend done");
        rv = findViewById(R.id.recycler_view_friends);
        homeIcon= findViewById(R.id.homeicon);
        logout = findViewById(R.id.usericon);
        notificon= findViewById(R.id.notificon);
        sessionManager = new SessionManager(getApplicationContext());

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterProfilesFriends(friends, this);
        rv.setAdapter(MyAdapter);
        MyAdapter.setonItemClickListener(FriendsListActivity.this);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(FriendsListActivity.this, HomeScreenActivity.class));
        });

        logout.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(FriendsListActivity.this, MainActivity.class));
        });

        notificon.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(FriendsListActivity.this, NotificationActivity.class));
        });


    }


    public void GetFriends(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/Friends/getFriendsByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        friends.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("persons");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                String firstname = object.getString("firstname").trim();
                                String email = object.getString("email").trim();
                                int age = object.getInt("age");

                                Person person = new Person(name,firstname,age,email);
                                friends.add(person);
                                MyAdapter.notifyDataSetChanged();
                                }
                            Log.d("friends", friends.toString() );
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
        Intent intent = new Intent(FriendsListActivity.this, FriendActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }

}