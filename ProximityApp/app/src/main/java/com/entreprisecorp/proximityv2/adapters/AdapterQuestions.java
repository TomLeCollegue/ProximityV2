package com.entreprisecorp.proximityv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.questions.Question;

import java.util.ArrayList;

public class AdapterQuestions extends RecyclerView.Adapter<AdapterQuestions.MyViewHolder1>{

    public ArrayList<Question> questions;
    private OnItemClickListener Listener;
    private Context context;

    public AdapterQuestions(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterQuestions.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterQuestions.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.question_item_recyclerview, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterQuestions.MyViewHolder1 holder, int position) {
        Question question = questions.get(position);
        holder.display(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView hobby;

        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.question_sentence);
            hobby = itemView.findViewById(R.id.question_hobby);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            Listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void display(Question question) {

            title.setText(question.getText());
            hobby.setText(question.getHobby());
        }

    }

}

