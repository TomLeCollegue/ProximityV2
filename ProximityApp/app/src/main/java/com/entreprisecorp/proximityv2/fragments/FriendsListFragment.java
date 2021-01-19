package com.entreprisecorp.proximityv2.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.FriendActivity;
import com.entreprisecorp.proximityv2.FriendsListActivity;
import com.entreprisecorp.proximityv2.HomeScreenActivity;
import com.entreprisecorp.proximityv2.MainActivity;
import com.entreprisecorp.proximityv2.NotificationActivity;
import com.entreprisecorp.proximityv2.Person;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.adapters.AdapterProfilesFriends;
import com.entreprisecorp.proximityv2.imagesManager.imagesConversion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class FriendsListFragment extends Fragment implements AdapterProfilesFriends.OnItemClickListener{

    View view;
    public static ArrayList<Person> friends = new ArrayList<Person>();
    private RecyclerView rv;
    private AdapterProfilesFriends MyAdapter;


    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        GetFriends(SessionManager.uuid);
        Log.d("rv", "Friend done");
        rv = view.findViewById(R.id.recycler_view_friends);
        sessionManager = new SessionManager(getContext());

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterProfilesFriends(friends, getContext());
        rv.setAdapter(MyAdapter);
        MyAdapter.setonItemClickListener(FriendsListFragment.this);


        return view;
    }

    public void GetFriends(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/Friends/getFriendsByUuid";
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
        Intent intent = new Intent(getContext(), FriendActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }

}