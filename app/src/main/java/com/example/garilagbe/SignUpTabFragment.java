package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpTabFragment extends Fragment {
    Button signBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        /*signBtn = view.findViewById(R.id.signup_button);

        signBtn.setOnClickListener(v ->{
            Intent i = new Intent(LoginTabFragment.this, home.class);
            startActivity(i);
        });*/

        return view;
    }
}