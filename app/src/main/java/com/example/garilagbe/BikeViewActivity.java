package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
    ImageView btnBack,btnPost,btnHome,btnFav;

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
         btnPost = findViewById(R.id.btn_post);
         btnHome = findViewById(R.id.btn_home);
        btnFav = findViewById(R.id.btn_fav);


        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BikeViewActivity.this, FavoritePostActivity.class));
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

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BikeViewActivity.this, UploadItemActivity.class));
            }
        });



        //load name
        TextView profileName = findViewById(R.id.profile_name);

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
                bikePostList.clear(); // clear old data
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if(post.getType().equals("Bike") && post.getStatus().equals("Available")){
                        post.setPostId(postSnap.getKey()); // ✅ Set postId from Firebase key
                        bikePostList.add(post);
                    }
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