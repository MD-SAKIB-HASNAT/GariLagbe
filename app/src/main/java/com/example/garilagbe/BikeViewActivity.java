package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeViewActivity extends AppCompatActivity {
    RecyclerView bikeRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> bikePostList; // Dynamic list to store posts

    ProgressBar progressBar;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bike_view);


        // Initialize RecyclerView
        bikeRecyclerView = findViewById(R.id.bike_recview);
        progressBar = findViewById(R.id.progressBarBike);
        bikeRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        bikePostList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(bikePostList,BikeViewActivity.this); // pass dynamic data
        bikeRecyclerView.setAdapter(vehicleAdapter);

         btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                bikePostList.clear(); // clear old data
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if(post.getType().equals("Bike")) bikePostList.add(post);
                }
                vehicleAdapter.notifyDataSetChanged(); // refresh RecyclerView
                progressBar.setVisibility(View.INVISIBLE); // Hides it but keeps the space


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log error or show Toast
            }
        });
    }
}