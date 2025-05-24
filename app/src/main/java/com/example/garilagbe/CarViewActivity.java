package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    RecyclerView carRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> carPostList; // Dynamic list to store posts

    ProgressBar progressBar;
    ImageView btnBack,btnPost,btnHome;
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize RecyclerView
        carRecyclerView = findViewById(R.id.car_recview);
        progressBar = findViewById(R.id.progressBarCar);
        ImageView filterIcon = findViewById(R.id.img_filter);

        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carPostList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(carPostList,CarViewActivity.this); // pass dynamic data
        carRecyclerView.setAdapter(vehicleAdapter);
        btnBack = findViewById(R.id.btn_back);

        btnPost = findViewById(R.id.btn_post);
        btnHome = findViewById(R.id.btn_home);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(CarViewActivity.this, UploadItemActivity.class));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //load name
        profileName = findViewById(R.id.profile_name);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    profileName.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log or show error if needed
            }
        });

        // Load posts from Firebase
        loadPostsFromFirebase();
    }




    // Fetch posts from Firebase Realtime Database
    private void loadPostsFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("posts");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                carPostList.clear(); // clear old data
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if (post != null && post.getType().equals("Car") && post.getStatus().equals("Available")) {
                        post.setPostId(postSnap.getKey()); // ✅ Set postId from Firebase key
                        carPostList.add(post);
                    }
                }
                vehicleAdapter.notifyDataSetChanged(); // refresh RecyclerView
                progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log error or show Toast
            }
        });
    }
}
