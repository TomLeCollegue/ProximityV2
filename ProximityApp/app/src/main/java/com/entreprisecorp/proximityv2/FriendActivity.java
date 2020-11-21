package com.entreprisecorp.proximityv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import com.entreprisecorp.proximityv2.adapters.AdapterProfilesFriends;
import com.entreprisecorp.proximityv2.adapters.AdapterQuestionResults;
import com.entreprisecorp.proximityv2.fragments.FriendsListFragment;
import com.entreprisecorp.proximityv2.questions.Question;
import com.entreprisecorp.proximityv2.questions.QuestionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class FriendActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SessionManager sessionManager;
    private AdapterQuestionResults MyAdapter;
    private int id;
    private Person friend;
    private TextView nameFriend;
    private ImageView profilePic;

    ArrayList<QuestionResult> questionResults = new ArrayList<QuestionResult>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            id = (int) bundle.get("id_profil");
            friend = FriendsListFragment.friends.get(id);
        }
        rv = findViewById(R.id.rv_questionResults);
        nameFriend = findViewById(R.id.person_name);
        profilePic = findViewById(R.id.friend_image);

        nameFriend.setText(friend.getFirstname() + " " + friend.getName());


        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterQuestionResults(questionResults, this);
        rv.setAdapter(MyAdapter);


        GetQuestion(friend.getEmail());
        downloadProfileImage(friend.getEmail());

    }



    public void GetQuestion(String email) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", SessionManager.uuid);
            jsonBody.put("email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/getResultQuizz";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(JSONObject response) {
                        questionResults.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("questionResults");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String text = object.getString("questionText").trim();
                                boolean bool = object.getBoolean("response");


                                QuestionResult questionResult = new QuestionResult(text, bool);
                                questionResults.add(questionResult);
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


    public void downloadProfileImage(String email){
        String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + email + "/downloadLow";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profilePic.setImageBitmap(response);
                profilePic.setVisibility(View.VISIBLE);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(PersonDiscoveredActivity.this, "Error while downloading image", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(request);
    }


}