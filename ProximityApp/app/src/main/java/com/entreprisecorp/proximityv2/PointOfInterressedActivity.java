package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.adapters.AdapterHobbies;
import com.entreprisecorp.proximityv2.adapters.AdapterQuestions;
import com.entreprisecorp.proximityv2.hobby.Hobby;
import com.entreprisecorp.proximityv2.hobby.Question;

import java.util.ArrayList;

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

        questions.add(new Question("Quel est le meilleur language de programmation du monde entier ? fait attention a toi BOBO","1","2","3","4","Informatique"));
        questions.add(new Question("Quel est le meilleur language de programmation du monde entier ? fait attention a toi BOBO","1","2","3","4","Informatique"));
        questions.add(new Question("Quel est le meilleur language de programmation du monde entier ? fait attention a toi BOBO","1","2","3","4","Informatique"));
        questions.add(new Question("Quel est le meilleur language de programmation du monde entier ? fait attention a toi BOBO","1","2","3","4","Informatique"));

        hobbies.add(new Hobby("Informatique",1500));
        hobbies.add(new Hobby("Tennis",800));
        hobbies.add(new Hobby("Jeux-Video",150));

        MyAdapter.notifyDataSetChanged();
        MyAdapterHobbies.notifyDataSetChanged();
    }

    public void onItemClick(int position) {
        Intent intent = new Intent(PointOfInterressedActivity.this, MainActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }

    public void onItemClickHobby(int position) {
        Intent intent = new Intent(PointOfInterressedActivity.this, MainActivity.class);
        intent.putExtra("id_profil", position);
        startActivity(intent);
    }
}