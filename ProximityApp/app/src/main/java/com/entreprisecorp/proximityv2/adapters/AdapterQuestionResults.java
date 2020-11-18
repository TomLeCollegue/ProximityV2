package com.entreprisecorp.proximityv2.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.questions.Question;
import com.entreprisecorp.proximityv2.questions.QuestionResult;

import java.util.ArrayList;

public class AdapterQuestionResults extends RecyclerView.Adapter<AdapterQuestionResults.MyViewHolder1>{

    public ArrayList<QuestionResult> questionResults;
    private OnItemClickListener Listener;
    private Context context;

    public AdapterQuestionResults(ArrayList<QuestionResult> questionResults, Context context) {
        this.questionResults = questionResults;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterQuestionResults.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterQuestionResults.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_question_result, parent, false);
        return new MyViewHolder1(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AdapterQuestionResults.MyViewHolder1 holder, int position) {
        QuestionResult questionResult = questionResults.get(position);
        holder.display(questionResult);
    }

    @Override
    public int getItemCount() {
        return questionResults.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final Button text;

        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.questiontext);


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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void display(QuestionResult questionResult) {

            text.setText(questionResult.getQuestionText());
            if(questionResult.isResponse()){
                text.setBackgroundTintList(context.getResources().getColorStateList(R.color.ColorGreen));
            }
            else{
                text.setBackgroundTintList(context.getResources().getColorStateList(R.color.ColorRed));
            }
        }

    }

}

