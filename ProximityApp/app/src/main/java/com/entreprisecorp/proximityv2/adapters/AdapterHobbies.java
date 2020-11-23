package com.entreprisecorp.proximityv2.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.hobby.Hobby;
import com.entreprisecorp.proximityv2.imagesManager.imagesConversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class AdapterHobbies extends RecyclerView.Adapter<AdapterHobbies.MyViewHolder1>{

    public ArrayList<Hobby> hobbies;
    private OnItemClickListener Listener;
    private Context context;
    private Bitmap profileImageBitmap;
    private BitmapDrawable profileImageBitmapDraw;
    private String filepath;

    private String TAG = "test";

    public AdapterHobbies(ArrayList<Hobby> hobbies, Context context) {
        this.hobbies = hobbies;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClickHobby(int position);
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
                            Listener.onItemClickHobby(position);
                        }
                    }
                }
            });
        }

        public void display(Hobby hobby) {

            points.setText(hobby.getPoints() + " ");
            name.setText(hobby.getName());
            downloadProfileImage(hobby.getName());

            filepath = context.getExternalFilesDir(null).getAbsolutePath();
            File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
            Log.d(TAG, dir.toString());
            File file = new File (dir, hobby.getName()+ "_pic.jpg");

            if (file.exists()){
                profileImageBitmapDraw = new BitmapDrawable(Resources.getSystem(), file.toString());
                displayProfileImage(profileImageBitmapDraw);
                Log.d(TAG, "it exists");
            } else {
                dir.mkdir();
                downloadProfileImage(hobby.getName());
                displayProfileImage(profileImageBitmapDraw);
                Log.d(TAG, "it doesnt exists");
            }



        }


        public void downloadProfileImage(String name){
            String urlDownload = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + name + "/downloadPicHobby";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    /*profilePic.setImageBitmap(response);
                    profilePic.setVisibility(View.VISIBLE);*/
                    profileImageBitmap = response;
                    hobbyPic.setImageBitmap(response);
                    profileImageBitmapDraw = new BitmapDrawable(Resources.getSystem(),profileImageBitmap);
                    saveToInternalStorage(profileImageBitmapDraw, name);
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

        private void saveToInternalStorage(BitmapDrawable bitmap_profile, String name){
            OutputStream outputStream = null;

            Bitmap bitmap = new imagesConversion().getBitmapFromDrawable(bitmap_profile);

            String filepath = context.getExternalFilesDir(null).getAbsolutePath();
            File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
            File file = new File (dir, name+ "_pic.jpg");
            try{
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
            new imagesConversion().compressImageToJpeg(bitmap,70,outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream fos = context.openFileOutput("profil", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                // write object to file
                //oos.writeObject(MainActivity.profil);
                // closing resources
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void displayProfileImage(BitmapDrawable profileImageBitmapDraw){

            hobbyPic.setImageDrawable(profileImageBitmapDraw);

        }

    }

}

