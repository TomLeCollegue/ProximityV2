package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.entreprisecorp.proximityv2.hobby.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class QuizzActivity extends AppCompatActivity {

    private Person personDiscovered;

    private ArrayList<Question> questions = new ArrayList<Question>();


    private ImageView friendPic;
    private ImageView hobbyPic;

    private TextView quizzName;
    private TextView hobbyName;

    private TextView questionNumber;
    private TextView questionText;

    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;
    private Button nextQuestion;

    private int numQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        friendPic = findViewById(R.id.friend_image);
        hobbyPic = findViewById(R.id.hobby_pic);

        quizzName = findViewById(R.id.quizzname);
        hobbyName = findViewById(R.id.hobby);

        questionNumber = findViewById(R.id.questionnumber);
        questionText = findViewById(R.id.questiontext);

        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        nextQuestion = findViewById(R.id.buttonnextQuestion);



        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            int id = (int) bundle.get("id_profil");
            personDiscovered = NotificationActivity.persons.get(id);
        }
        quizzName.setText("Quizz de " + personDiscovered.getFirstname());


        GetQuestion(personDiscovered.getEmail());
        Log.d("questions", questions.toString());
        downloadProfileImage(personDiscovered.getEmail());


        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numQuestion < (questions.size() -1)){
                    numQuestion ++;
                    DisplayQuestion();
                }
                else {
                    startActivity(new Intent(QuizzActivity.this, HomeScreenActivity.class));

                    //TODO sendAnswer
                }
            }
        });


    }


    public void GetQuestion(String email) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/GetQuestionByEmail";
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
                            numQuestion = 0;
                            Log.d("questions", questions.toString());

                            DisplayQuestion();
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
                friendPic.setImageBitmap(response);
                friendPic.setVisibility(View.VISIBLE);
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
    public void downloadHobbyImage(String name){
        String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + name + "/downloadPicHobby";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                hobbyPic.setImageBitmap(response);
                hobbyPic.setVisibility(View.VISIBLE);
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


    public void DisplayQuestion(){
        Question question = questions.get(numQuestion);

        downloadHobbyImage(question.getHobby());
        hobbyName.setText(question.getHobby());

        questionNumber.setText("Question " + (numQuestion + 1));

        questionText.setText(question.getText());
        answer1.setText(question.getChoice1());
        answer2.setText(question.getChoice2());
        answer3.setText(question.getChoice3());
        answer4.setText(question.getAnswer());

        if(numQuestion < (questions.size() -1)){
            nextQuestion.setText("Question Suivante");
        }
        else{
            nextQuestion.setText("Fin du Quizz");
        }
    }
}