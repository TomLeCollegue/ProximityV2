package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.questions.Question;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateQuestionActivity extends AppCompatActivity {

    private static final String TAG ="test";
    private TextView hobbyText;
    private EditText updateQuestiontext;
    private EditText updateChoice1;
    private EditText updateChoice2;
    private EditText updateChoice3;
    private EditText updateAnswer;
    private Button updateQuestionBtn;

    private Question question;

    private SessionManager sessionManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_question);

        Intent infoIntent = getIntent();
        Bundle bundle = infoIntent.getExtras();

        if(bundle != null){
            int id = (int) bundle.get("index_question");
            question = PointOfInterressedActivity.questions.get(id);
        }

        Log.d(TAG, question.getHobby());

        sessionManager = new SessionManager(getApplicationContext());

        hobbyText = findViewById(R.id.hobbyTextUpdateQuestion);
        updateQuestiontext = findViewById(R.id.question_text_update);
        updateChoice1 = findViewById(R.id.choice1_update);
        updateChoice2 = findViewById(R.id.choice2_update);
        updateChoice3 = findViewById(R.id.choice3_update);
        updateAnswer = findViewById(R.id.answer_update);
        updateQuestionBtn = findViewById(R.id.buttonUpdateQuestion);

        hobbyText.setText(question.getHobby());
        updateQuestiontext.setText(question.getText());
        updateChoice1.setText(question.getChoice1());
        updateChoice2.setText(question.getChoice2());
        updateChoice3.setText(question.getChoice3());
        updateAnswer.setText(question.getAnswer());


        updateQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(SessionManager.uuid, updateQuestiontext.getText().toString(), updateChoice1.getText().toString(), updateChoice2.getText().toString(), updateChoice3.getText().toString(), updateAnswer.getText().toString() ,question.getHobby(), question.getUuidQuestion());
            }
        });


    }


    public void updateQuestion(String uuid, String questiontext, String choice1, String choice2, String choice3, String answer, String hobby, String uuidQuestion){
        RequestQueue requestQueue =  Volley.newRequestQueue(getApplicationContext());
        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/ModifyQuestion";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
            jsonBody.put("text", questiontext);
            jsonBody.put("choice1", choice1);
            jsonBody.put("choice2", choice2);
            jsonBody.put("choice3", choice3);
            jsonBody.put("answer", answer);
            jsonBody.put("hobby", hobby);
            jsonBody.put("uuidQuestion", uuidQuestion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "test avant la requete");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*startActivity(new Intent(UpdateQuestionActivity.this, PointOfInterressedActivity.class));
                        finish();*/
                        Toast.makeText(getApplicationContext(), "Question modifiée", Toast.LENGTH_SHORT ).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}