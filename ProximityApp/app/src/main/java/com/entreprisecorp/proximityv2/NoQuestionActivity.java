package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoQuestionActivity extends AppCompatActivity {

    private Button CreateQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_question);

        CreateQuestion = findViewById(R.id.button_create_question);

        CreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoQuestionActivity.this, AddQuestionActivity.class));
                finish();
            }
        });
    }
}