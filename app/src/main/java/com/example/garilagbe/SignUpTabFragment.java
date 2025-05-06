package com.example.garilagbe;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpTabFragment extends Fragment {

    EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    Button signBtn;
    String name, email, password, confirmPassword;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        nameInput = view.findViewById(R.id.signup_name);
        emailInput = view.findViewById(R.id.signup_email);
        passwordInput = view.findViewById(R.id.signup_password);
        confirmPasswordInput = view.findViewById(R.id.signup_confirm_password);
        signBtn = view.findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        signBtn.setOnClickListener(v -> handleSignUp());

        return view;
    }

    private void handleSignUp() {
        name = nameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }

        if (!isValidEmail(email)) {
            emailInput.setError("Invalid email format");
            return;
        }

        if (!isComplexPassword(password)) {
            passwordInput.setError("Password must be 8+ chars, 1 upper, 1 number, 1 symbol");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        signUpUser(email, password);
    }

    private void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToRealtimeDB(user.getUid(), name, email);
                        }
                    } else {
                        // Get the exception and show it
                        Exception e = task.getException();
                        if (e != null) {
                            showToast("Sign up failed: " + e.getMessage());
                            Log.e("SignUpError", "SignUp failed", e);
                        }
                    }
                });
    }


    private void saveUserToRealtimeDB(String uid, String name, String email) {
        UserModel user = new UserModel(name, email);

        dbRef.child(uid).setValue(user)
                .addOnSuccessListener(unused -> {
                    showToast("Sign up successful");
                    // TODO: Navigate to home screen
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to save user: " + e.getMessage());
                });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isComplexPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
