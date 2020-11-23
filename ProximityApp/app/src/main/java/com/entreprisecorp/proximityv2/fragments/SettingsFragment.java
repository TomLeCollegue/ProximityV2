package com.entreprisecorp.proximityv2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.entreprisecorp.proximityv2.MainActivity;
import com.entreprisecorp.proximityv2.R;
import com.entreprisecorp.proximityv2.accounts.SessionManager;

public class SettingsFragment extends Fragment {

    View view;
    private SessionManager sessionManager;
    private Button logoutBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_settings, container, false);

        sessionManager = new SessionManager(getContext());
        logoutBtn = view.findViewById(R.id.log_out);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.Logout();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });



        return view;
    }
}