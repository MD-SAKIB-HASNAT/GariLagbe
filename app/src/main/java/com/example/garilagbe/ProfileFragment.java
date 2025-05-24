package com.example.garilagbe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    LinearLayout btnPost,sellHistory;
    Button ownPost;
    Button btnProfile;
    private ImageView profileImageView;
    private TextView nameView, phoneView, addressView, postalView, userNameView, emailView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnPost = view.findViewById(R.id.btn_post);
        ownPost = view.findViewById(R.id.own_post);
        btnProfile = view.findViewById(R.id.btn_profile);

        profileImageView = view.findViewById(R.id.profile_pic);
        nameView = view.findViewById(R.id.txt_fullName);
        phoneView = view.findViewById(R.id.txt_phone);
        addressView = view.findViewById(R.id.txt_address);
        postalView = view.findViewById(R.id.txt_postal);
        userNameView = view.findViewById(R.id.user_name);
        emailView = view.findViewById(R.id.user_email);
        sellHistory = view.findViewById(R.id.sellHistory);

        sellHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SellHistoryActivity.class));
            }
        });

        loadUserProfile();

        ownPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OwnPostActivity.class));
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadItemActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SettingsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SettingsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void loadUserProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load from 'userprofile' node
        DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference("userprofile").child(uid);
        userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameView.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    phoneView.setText(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                    addressView.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                    postalView.setText(Objects.requireNonNull(snapshot.child("postal").getValue()).toString());

                    String base64Image = Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString();
                    if (!base64Image.isEmpty()) {
                        byte[] decoded = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                        profileImageView.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Load username and email from 'users' node
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userNameView.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    emailView.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
