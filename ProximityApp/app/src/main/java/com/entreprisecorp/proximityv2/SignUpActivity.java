package com.entreprisecorp.proximityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.entreprisecorp.proximityv2.accounts.SessionManager;
import com.entreprisecorp.proximityv2.imagesManager.imagesConversion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    // déclaration items vues
    private ImageView profileImage;
    private EditText name;
    private EditText surname;
    private EditText age;
    private EditText email;
    private EditText password;
    private Button signUpBtn;

    private SessionManager sessionManager;

    private Context mContext;

    private String TAG = "test";

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sessionManager = new SessionManager(getApplicationContext());

        // déclaration variables vue
        profileImage = (ImageView) findViewById(R.id.profile_image_signup);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        age = (EditText) findViewById(R.id.age);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signUpBtn = (Button) findViewById(R.id.buttonsignup);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                profileImage.startAnimation(animation);
                openGallery();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String surnameText = surname.getText().toString();
                //int ageInt = Integer.parseInt(age.getText().toString());
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                //signUp(nameText, surnameText, ageInt, emailText, passwordText);

                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
                Bitmap myImageBitmap = new imagesConversion().getBitmapFromDrawable(drawable);

                byte[] bytearrayrequest = getBytesFromBitmap(myImageBitmap);

                addProfileImage(emailText, bytearrayrequest);
            }
        });

    }

    private void signUp(String name, String surname, int age, String email, String password) {

        RequestQueue requestQueue =  Volley.newRequestQueue(getApplicationContext());
        String URL = "http://"+ SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/account/signUP";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("firstname", surname);
            jsonBody.put("age", age);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "test avant la requete");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("response").equals(email)){
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }
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


    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }



    private void addProfileImage(String mail, byte[] profileImage ) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://" + SessionManager.IPSERVER + "/RestFullTEST-1.0-SNAPSHOT/images/" + mail + "/upload";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SignUpActivity.this, "Image envoyée avec succes", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse : ça marche pas");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return profileImage;
            }

        };
        requestQueue.add(stringRequest);
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageURI = data.getData();
            profileImage.setImageURI(imageURI);
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}