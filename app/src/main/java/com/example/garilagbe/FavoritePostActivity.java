package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garilagbe.Post;
import com.example.garilagbe.VehicleAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritePostActivity extends AppCompatActivity {

    ImageView btnBack;
    RecyclerView favRecyclerView;
    ProgressBar progressBar;

    List<Post> favPostList;
    VehicleAdapter vehicleAdapter;

    DatabaseReference postRef;
    DatabaseReference favRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_post);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        favRecyclerView = findViewById(R.id.fav_recview);
        progressBar = findViewById(R.id.progressBarBike);

        // Back button action
        btnBack.setOnClickListener(view -> finish());

        // RecyclerView setup
        favRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favPostList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(favPostList, FavoritePostActivity.this);
        favRecyclerView.setAdapter(vehicleAdapter);

        // Firebase setup
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference("favorite").child(currentUserId);
        postRef = FirebaseDatabase.getInstance().getReference("posts"); // assuming your post data is under 'posts'

        // Load favorite posts
        loadFavoritePosts();
    }

    private void loadFavoritePosts() {
        progressBar.setVisibility(View.VISIBLE);
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favPostList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    fetchPostById(postId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchPostById(String postId) {
        postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    post.setPostId(postId);
                    favPostList.add(post);
                    vehicleAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
