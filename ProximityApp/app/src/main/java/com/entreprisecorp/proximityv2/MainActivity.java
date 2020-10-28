package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mail;
    private TextView password;
    private Button connectionButton;

    private String mailText;
    private String passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------link with view-----------------------------//
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        connectionButton = findViewById(R.id.buttonconnection);


        //----------Listener Button----------------------------//
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText = password.getText().toString();
                mailText = mail.getText().toString();

                Connection(mailText,passwordText);

            }
        });





    }


    public void Connection(String mail, String password){

        String URL = "";


    }
}