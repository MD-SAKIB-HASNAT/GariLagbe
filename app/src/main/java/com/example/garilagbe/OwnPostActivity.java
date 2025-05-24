package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class OwnPostActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyPostsAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private ProgressBar loadingProgressBar;
    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_own_post);


        recyclerView = findViewById(R.id.myPostsRecyclerView);
        btnBack = findViewById(R.id.btn_back);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        recyclerView.setLayoutManager(new GridLayoutManager(OwnPostActivity.this, 1));

        adapter = new MyPostsAdapter(postList,OwnPostActivity.this);
        recyclerView.setAdapter(adapter);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadUserPosts();
    }

  private void loadUserPosts() {
        loadingProgressBar.setVisibility(View.VISIBLE); // Show the loader

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if (post != null && uid.equals(post.getUserId()) && post.getStatus().equals("Available")) {
                        post.setPostId(postSnap.getKey());
                        postList.add(post);
                    }
                }

                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE); // Hide the loader
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingProgressBar.setVisibility(View.GONE); // Hide the loader
                Toast.makeText(OwnPostActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

}