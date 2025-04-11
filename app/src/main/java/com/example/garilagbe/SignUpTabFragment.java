package com.example.garilagbe;

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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignUpTabFragment extends Fragment {

    EditText emailInput, passwordInput, confirmPasswordInput;
    Button signBtn;
    String password,email,confirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        emailInput = view.findViewById(R.id.signup_email);
        passwordInput = view.findViewById(R.id.signup_password);
        confirmPasswordInput = view.findViewById(R.id.signup_confirm_password);
        signBtn = view.findViewById(R.id.signup_button);

        signBtn.setOnClickListener(v -> handleSignUp());

        return view;
    }

    private void handleSignUp() {
         email = emailInput.getText().toString().trim();
         password = passwordInput.getText().toString().trim();
         confirmPassword = confirmPasswordInput.getText().toString().trim();

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


        // Send OTP
       // sendEmailOtp(email);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isComplexPassword(String password) {
        // At least 8 chars, 1 upper, 1 number, 1 special
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void sendEmailOtp(String email) {
       /* new Thread(() -> {
            try {
                URL url = new URL("https://yourdomain.com/send_otp.php"); // Replace with your real PHP endpoint
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "email=" + URLEncoder.encode(email, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject json = new JSONObject(result.toString());
                String status = json.getString("status");

                requireActivity().runOnUiThread(() -> {
                    if (status.equals("success")) {
                        showToast("OTP sent to email!");
                        // Navigate to OTP verification screen (optional)
                    } else {
                        showToast("Failed to send OTP");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> showToast("Error sending OTP"));
            }
        }).start();*/
       /* FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Toast.makeText(getContext(), "Verification email sent", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed to send verification", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/

    }
}
