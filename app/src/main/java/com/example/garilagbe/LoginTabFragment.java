package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginTabFragment extends Fragment {
    private EditText edtEmail, edtPass;
    private Button loginButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = view.findViewById(R.id.login_user);
        edtPass = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> handleLogin());

        return view;
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (!isValidEmail(email)) {
            edtEmail.setError("Invalid email format");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPass.setError("Password is required");
            return;
        }

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            showToast("Login successful");
                            Intent i = new Intent(getActivity(), MainHome.class);
                            startActivity(i);
                            requireActivity().finish();  // Optional: close login screen
                        }
                    } else {
                        showToast("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
