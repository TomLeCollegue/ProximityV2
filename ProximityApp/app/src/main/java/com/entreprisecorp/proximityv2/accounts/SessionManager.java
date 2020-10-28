package com.entreprisecorp.proximityv2.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.entreprisecorp.proximityv2.MainActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    public static String PASSWORD = "password";
    public static String EMAIL = "email";
    public static String LOGIN = "login";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN", 0);
        editor = sharedPreferences.edit();
    }

    public void CreateSession(String email, String password){
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }

    public void Logout(){
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();

        user.put(PASSWORD, sharedPreferences.getString(PASSWORD,null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        return user;
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }



}
