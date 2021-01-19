package com.entreprisecorp.proximityv2.fragments;

import android.content.Intent;
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
import com.entreprisecorp.proximityv2.DiscoveryActivity;
import com.entreprisecorp.proximityv2.NotificationActivity;
import com.entreprisecorp.proximityv2.Person;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.adapters.AdapterNotifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationFragments extends Fragment implements AdapterNotifications.OnItemClickListener{
    View view;
    public static ArrayList<Person> persons = new ArrayList<Person>();
    private RecyclerView rv;
    private AdapterNotifications MyAdapter;
    private ImageView homeIcon;
    private ImageView logout;
    private ImageView messages;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_notification_fragments, container, false);

        GetNotif(SessionManager.uuid);
        Log.d("rv", "Friend done");
        rv = view.findViewById(R.id.recycler_view_friends);
        homeIcon= view.findViewById(R.id.homeicon);
        logout = view.findViewById(R.id.usericon);
        messages = view.findViewById(R.id.messages);
        sessionManager = new SessionManager(getContext());

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterNotifications(persons, getContext());
        rv.setAdapter(MyAdapter);
        MyAdapter.setonItemClickListener(NotificationFragments.this);

        return view;

    }

    public void GetNotif(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/Friends/getDiscoveredByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        persons.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("persons");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                String firstname = object.getString("firstname").trim();
                                String email = object.getString("email").trim();
                                int age = object.getInt("age");

                                Person person = new Person(name,firstname,age,email);
                                persons.add(person);

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
        Intent intent = new Intent(getContext(), DiscoveryActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }

}