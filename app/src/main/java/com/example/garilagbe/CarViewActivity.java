package com.example.garilagbe;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    RecyclerView carRecyclerView;
    CarAdapter carAdapter;
    List<Post> postList; // Dynamic list to store posts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize RecyclerView
        carRecyclerView = findViewById(R.id.car_recview);
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        postList = new ArrayList<>();
        carAdapter = new CarAdapter(postList); // pass dynamic data
        carRecyclerView.setAdapter(carAdapter);

        // Load posts from Firebase
        loadPostsFromFirebase();
    }

    // Fetch posts from Firebase Realtime Database
    private void loadPostsFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("posts");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear(); // clear old data
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    postList.add(post);
                }
                carAdapter.notifyDataSetChanged(); // refresh RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log error or show Toast
            }
        });
    }
}
