package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    RecyclerView carRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> carPostList; // Dynamic list to store posts

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize RecyclerView
        carRecyclerView = findViewById(R.id.car_recview);
        progressBar = findViewById(R.id.progressBarCar);
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carPostList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(carPostList); // pass dynamic data
        carRecyclerView.setAdapter(vehicleAdapter);

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
                    if(post.getType().equals("Car")) carPostList.add(post);
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
