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

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyViewHolder1>{

    public ArrayList<Person> friends;
    private OnItemClickListener Listener;
    private Context context;

    public AdapterNotifications(ArrayList<Person> profils, Context context) {
        this.friends = profils;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setonItemClickListener(AdapterNotifications.OnItemClickListener listener)
    {
        Listener = listener;
    }


    @NonNull
    @Override
    public AdapterNotifications.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.notif_item_recyclerview, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotifications.MyViewHolder1 holder, int position) {
        Person person = friends.get(position);
        holder.display(person);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView profilePic;
        private final TextView subtitle;

        public MyViewHolder1(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.name_notif);
            profilePic = itemView.findViewById(R.id.friend_image);
            subtitle = itemView.findViewById(R.id.subtitle);



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

        public void display(Person person) {

            title.setText(person.getFirstname() + " " + person.getName());
            subtitle.setText("est à proximité");
            downloadProfileImage(person.getEmail());


        }


        public void downloadProfileImage(String email){
            String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + email + "/downloadLow";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    profilePic.setImageBitmap(response);
                    profilePic.setVisibility(View.VISIBLE);
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

