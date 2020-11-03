package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.google.gson.JsonObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity {

    private static final String TAG = "test";
    private EditText questionText;
    private EditText choice1;
    private EditText choice2;
    private EditText choice3;
    private EditText answer;

    private TextView hobbyTextView;

    private Button addQuestionBtn;


    private SessionManager sessionManager;

    private ArrayList<String> hobbies = new ArrayList<String>();
    private String[] hobbiesStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        sessionManager = new SessionManager(getApplicationContext());

        getHobbies(SessionManager.uuid);

        questionText = findViewById(R.id.question_text);

        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        answer = findViewById(R.id.answer);

        hobbyTextView = findViewById(R.id.hobbyText);

        addQuestionBtn = findViewById(R.id.buttonAddQuestion);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion(SessionManager.uuid, questionText.getText().toString(), choice1.getText().toString(), choice2.getText().toString(),choice3.getText().toString(), answer.getText().toString(), hobbyTextView.getText().toString());
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private void showAlertDialog() {

        Log.d(TAG, hobbies.toString());

        hobbiesStringList = new String[hobbies.size()];
        hobbiesStringList = hobbies.toArray(hobbiesStringList);

        for (String s : hobbiesStringList)
            Log.d(TAG,s);

        Log.d(TAG, "test");

        AlertDialog.Builder hobbyalertBuilder = new AlertDialog.Builder(AddQuestionActivity.this);
        hobbyalertBuilder.setTitle("Hobby de votre question");

        int checkedItem = 1; // hobby 1
        hobbyalertBuilder.setSingleChoiceItems(hobbiesStringList, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hobbyTextView.setText(hobbiesStringList[which]);
            }
        });


        hobbyalertBuilder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = hobbyalertBuilder.create();
        dialog.show();
    }


    private void addQuestion(String uuid, String questiontext, String choice1, String choice2, String choice3, String answer, String hobby) {

        RequestQueue requestQueue =  Volley.newRequestQueue(getApplicationContext());
        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/questions/CreateQuestion";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
            jsonBody.put("text", questiontext);
            jsonBody.put("choice1", choice1);
            jsonBody.put("choice2", choice2);
            jsonBody.put("choice3", choice3);
            jsonBody.put("answer", answer);
            jsonBody.put("hobby", hobby);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "test avant la requete");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       startActivity(new Intent(AddQuestionActivity.this, PointOfInterressedActivity.class));
                       finish();
                       Toast.makeText(getApplicationContext(), "Question ajoutée", Toast.LENGTH_SHORT ).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    private void getHobbies(String uuid) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/hobbies/GetAllHobbies";
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hobbies.clear();
                        try {
                            JSONArray jsonArray = response.getJSONArray("hobbies");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String hobby = object.getString("name").trim();
                                hobbies.add(hobby);
                            }
                            Log.d(TAG, hobbies.toString() );
                            showAlertDialog();
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
    }