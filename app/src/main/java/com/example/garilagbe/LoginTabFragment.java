package com.example.garilagbe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Calendar;


public class LoginTabFragment extends Fragment {
    private EditText edtUser, edtPass;
    private Button loginButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        edtUser = view.findViewById(R.id.login_user);
        edtPass = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> handleLogin());

        return view;
    }
    private void handleLogin() {
        String user = edtUser.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (user.isEmpty() || password.isEmpty()) {
            edtUser.setError("Enter valid email");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            edtPass.setError("Password must be at least 6 characters");
            return;
        }

        FirebaseAuth mAuth = null;
        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

}