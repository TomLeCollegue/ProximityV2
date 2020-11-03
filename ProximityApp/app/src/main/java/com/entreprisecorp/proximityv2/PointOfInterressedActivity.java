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
import com.entreprisecorp.proximityv2.adapters.AdapterHobbies;
import com.entreprisecorp.proximityv2.adapters.AdapterQuestions;
import com.entreprisecorp.proximityv2.hobby.Hobby;
import com.entreprisecorp.proximityv2.hobby.Question;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class PointOfInterressedActivity extends AppCompatActivity implements AdapterQuestions.OnItemClickListener,AdapterHobbies.OnItemClickListener {

    public static ArrayList<Question> questions = new ArrayList<Question>();
    public static ArrayList<Hobby> hobbies = new ArrayList<Hobby>();

    private RecyclerView rv_questions;
    private RecyclerView rv_hobbies;
    private AdapterQuestions MyAdapter;
    private AdapterHobbies MyAdapterHobbies;
    private ImageView homeIcon;
    private ImageView logout;
    private ImageView notificon;

    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_interressed);

        rv_questions = findViewById(R.id.rv_question);
        rv_hobbies = findViewById(R.id.rv_hobbies);

        homeIcon= findViewById(R.id.homeicon);
        logout = findViewById(R.id.usericon);
        notificon= findViewById(R.id.notificon);
        sessionManager = new SessionManager(getApplicationContext());

        rv_questions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_hobbies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterQuestions(questions, this);
        MyAdapterHobbies = new AdapterHobbies(hobbies, this);


        rv_questions.setAdapter(MyAdapter);
        rv_hobbies.setAdapter(MyAdapterHobbies);

        MyAdapter.setonItemClickListener(PointOfInterressedActivity.this);
        MyAdapterHobbies.setonItemClickListener(PointOfInterressedActivity.this);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(PointOfInterressedActivity.this, HomeScreenActivity.class));
        });

        logout.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(PointOfInterressedActivity.this, MainActivity.class));
        });

        notificon.setOnClickListener(v -> {
            sessionManager.Logout();
            startActivity(new Intent(PointOfInterressedActivity.this, NotificationActivity.class));
        });

        GetHobbies(SessionManager.uuid);
        GetQuestion(SessionManager.uuid);
    }

    public void onItemClick(int position) {
        Intent intent = new Intent(PointOfInterressedActivity.this, MainActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }

    public void onItemClickHobby(int position) {
        Intent intent = new Intent(PointOfInterressedActivity.this, FriendsListActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }


    public void GetHobbies(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/hobbies/GetHobbyByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hobbies.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("hobbies");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                int points = object.getInt("xp");

                                Hobby hobby = new Hobby(name,points);
                                hobbies.add(hobby);
                            }

                            //Test
                            //hobbies.add(new Hobby("Informatique", 30));
                            //hobbies.add(new Hobby("Informatique", -1));

                            //Sort by experience the hobbies
                            Collections.sort(hobbies, new Comparator<Hobby>(){
                                public int compare(Hobby h1, Hobby h2){
                                    if(h1.getPoints() == h2.getPoints())
                                        return 0;
                                    return h1.getPoints() < h2.getPoints() ? -1 : 1;
                                }
                            });

                            MyAdapterHobbies.notifyDataSetChanged();
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

    public void GetQuestion(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/GetQuestionByUuid";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        questions.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("questions");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String text = object.getString("text").trim();
                                String uuidQuestion = object.getString("uuidQuestion").trim();
                                String choice1 = object.getString("choice1").trim();
                                String choice2 = object.getString("choice2").trim();
                                String choice3 = object.getString("choice3").trim();
                                String answer = object.getString("answer").trim();
                                String hobby = object.getString("hobby").trim();

                                Question question = new Question(text, choice1, choice2, choice3, answer, hobby, uuidQuestion);
                                questions.add(question);
                            }

                            MyAdapter.notifyDataSetChanged();
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
    
}
