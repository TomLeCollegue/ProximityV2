package com.entreprisecorp.proximityv2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.AddQuestionActivity;
import com.entreprisecorp.proximityv2.FriendsListActivity;
import com.entreprisecorp.proximityv2.HomeScreenActivity;
import com.entreprisecorp.proximityv2.MainActivity;
import com.entreprisecorp.proximityv2.NotificationActivity;
import com.entreprisecorp.proximityv2.PointOfInterressedActivity;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.UpdateQuestionActivity;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.adapters.AdapterHobbies;
import com.entreprisecorp.proximityv2.adapters.AdapterProfilesFriends;
import com.entreprisecorp.proximityv2.adapters.AdapterQuestions;
import com.entreprisecorp.proximityv2.hobby.Hobby;
import com.entreprisecorp.proximityv2.questions.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserFragment extends Fragment implements AdapterQuestions.OnItemClickListener,AdapterHobbies.OnItemClickListener {
    View view;

    public static ArrayList<Question> questions = new ArrayList<Question>();
    public static ArrayList<Hobby> hobbies = new ArrayList<Hobby>();

    private RecyclerView rv_questions;
    private RecyclerView rv_hobbies;
    private AdapterQuestions MyAdapter;
    private AdapterHobbies MyAdapterHobbies;

    private ImageView addQuestion;

    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_user, container, false);

        rv_questions = view.findViewById(R.id.rv_question);
        rv_hobbies = view.findViewById(R.id.rv_hobbies);

        addQuestion = view.findViewById(R.id.add_question);

        sessionManager = new SessionManager(getContext());

        rv_questions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_hobbies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterQuestions(questions, getContext());
        MyAdapterHobbies = new AdapterHobbies(hobbies, getContext());


        rv_questions.setAdapter(MyAdapter);
        rv_hobbies.setAdapter(MyAdapterHobbies);

        MyAdapter.setonItemClickListener(UserFragment.this);
        MyAdapterHobbies.setonItemClickListener(UserFragment.this);

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
                addQuestion.startAnimation(animation);
                startActivity(new Intent(getContext(), AddQuestionActivity.class));
            }
        });

        GetHobbies(SessionManager.uuid);
        GetQuestion(SessionManager.uuid);

        return view;
    }

    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), UpdateQuestionActivity.class);
        intent.putExtra("index_question", position);
        startActivity(intent);
    }

    public void onItemClickHobby(int position) {
        Intent intent = new Intent(getContext(), FriendsListActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }


    public void GetHobbies(String uuid) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                                    return h1.getPoints() > h2.getPoints() ? -1 : 1;
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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