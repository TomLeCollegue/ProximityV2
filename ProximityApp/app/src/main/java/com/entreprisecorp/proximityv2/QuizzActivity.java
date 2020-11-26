package com.entreprisecorp.proximityv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.fragments.NotificationFragments;
import com.entreprisecorp.proximityv2.questions.Question;
import com.entreprisecorp.proximityv2.questions.QuestionAnswers;
import com.entreprisecorp.proximityv2.questions.uuidAnswer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private QuestionAnswers questionAnswers;

    private boolean response = false;

    private String[] answers;
    private boolean answered = false;


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

        //init answers
        questionAnswers = new QuestionAnswers();
        questionAnswers.setUuid(SessionManager.uuid);



        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            int id = (int) bundle.get("id_profil");
            personDiscovered = NotificationFragments.persons.get(id);
        }

        quizzName.setText("Quizz de " + personDiscovered.getFirstname());


        GetQuestion(personDiscovered.getEmail());
        Log.d("questions", questions.toString());
        downloadProfileImage(personDiscovered.getEmail());


        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                questionAnswers.getAnswers().add(new uuidAnswer(questions.get(numQuestion).getUuidQuestion(), response));

                if(numQuestion < (questions.size() -1)){
                    numQuestion ++;
                    DisplayQuestion();
                }
                else {
                    startActivity(new Intent(QuizzActivity.this, HomeScreenActivityFragments.class));
                    SendAnswer();
                }
            }
        });


        answer1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(!answered){
                    answered = true;
                    if (answers[0] == questions.get(numQuestion).getAnswer()) {
                        answer1.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
                        response = true;
                    }
                    else {
                        answer1.setBackgroundTintList(getResources().getColorStateList(R.color.ColorRed));
                        ColorInGreenNiceAnswer();
                    }
                }

            }
        });
        answer2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(!answered){
                    answered = true;
                    if (answers[1] == questions.get(numQuestion).getAnswer()) {
                        answer2.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
                        response = true;
                    }
                    else {
                        answer2.setBackgroundTintList(getResources().getColorStateList(R.color.ColorRed));
                        ColorInGreenNiceAnswer();
                    }
                }

            }
        });
        answer3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(!answered){
                    answered = true;
                    if (answers[2] == questions.get(numQuestion).getAnswer()) {
                        answer3.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
                        response = true;
                    }
                    else {
                        answer3.setBackgroundTintList(getResources().getColorStateList(R.color.ColorRed));
                        ColorInGreenNiceAnswer();
                    }
                }

            }
        });
        answer4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(!answered){
                    answered = true;
                    if (answers[3] == questions.get(numQuestion).getAnswer()) {
                        answer4.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
                        response = true;
                    }
                    else {
                        answer4.setBackgroundTintList(getResources().getColorStateList(R.color.ColorRed));
                        ColorInGreenNiceAnswer();
                    }
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
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void DisplayQuestion(){

        response = false;
        answered = false;
        Question question = questions.get(numQuestion);
        answer1.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));
        answer2.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));
        answer3.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));
        answer4.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));


        answers = new String[]{question.getChoice1(), question.getChoice2(), question.getChoice3(), question.getAnswer()};
        List<String> answerList = Arrays.asList(answers);
        Collections.shuffle(answerList);
        answerList.toArray(answers);


        downloadHobbyImage(question.getHobby());
        hobbyName.setText(question.getHobby());

        questionNumber.setText("Question " + (numQuestion + 1));

        questionText.setText(question.getText());
        answer1.setText(answers[0]);
        answer2.setText(answers[1]);
        answer3.setText(answers[2]);
        answer4.setText(answers[3]);

        if(numQuestion < (questions.size() -1)){
            nextQuestion.setText("Question Suivante");
        }
        else{
            nextQuestion.setText("Fin du Quizz");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ColorInGreenNiceAnswer(){

        if (answers[0] == questions.get(numQuestion).getAnswer()) {
            answer1.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
        }
        else if (answers[1] == questions.get(numQuestion).getAnswer()) {
            answer2.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
        }
        else if (answers[2] == questions.get(numQuestion).getAnswer()) {
            answer3.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
        }
        else if (answers[3] == questions.get(numQuestion).getAnswer()) {
            answer4.setBackgroundTintList(getResources().getColorStateList(R.color.ColorGreen));
        }
    }

    private void SendAnswer() {

        RequestQueue requestQueue =  Volley.newRequestQueue(getApplicationContext());
        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/answerQuestions";

        Gson gson = new Gson();
        String jsonObjectString = gson.toJson(questionAnswers);
        Log.d("debug", jsonObjectString);

        JSONObject jsonObject = new  JSONObject();
        try{
            jsonObject = new JSONObject(jsonObjectString);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Question repondues", Toast.LENGTH_SHORT ).show();
                        AcceptPerson();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }



    private void AcceptPerson(){
        RequestQueue requestQueue =  Volley.newRequestQueue(getApplicationContext());
        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/Friends/AcceptPerson";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", SessionManager.uuid);
            jsonBody.put("emailPerson", personDiscovered.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Personne acceptée", Toast.LENGTH_SHORT ).show();
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