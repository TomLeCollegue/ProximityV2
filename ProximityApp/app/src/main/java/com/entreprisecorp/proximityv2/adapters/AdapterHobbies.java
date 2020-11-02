package com.entreprisecorp.proximityv2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.Person;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.hobby.Hobby;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class AdapterHobbies extends RecyclerView.Adapter<AdapterHobbies.MyViewHolder1>{

    public ArrayList<Hobby> hobbies;
    private OnItemClickListener Listener;
    private Context context;

    public AdapterHobbies(ArrayList<Hobby> hobbies, Context context) {
        this.hobbies = hobbies;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterHobbies.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterHobbies.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.hobby_item_recyclerview, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHobbies.MyViewHolder1 holder, int position) {
        Hobby hobby = hobbies.get(position);
        holder.display(hobby);
    }

    @Override
    public int getItemCount() {
        return hobbies.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView points;
        private final ImageView hobbyPic;

        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_hobby);
            points = itemView.findViewById(R.id.exp_hobby);
            hobbyPic = itemView.findViewById(R.id.hobby_pic);



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

        public void display(Hobby hobby) {

            points.setText("Points : " + hobby.getPoints());
            name.setText(hobby.getName());
            downloadProfileImage(hobby.getName());



        }


        public void downloadProfileImage(String name){
            String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + name + "/downloadPicHobby";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
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
    }

}

