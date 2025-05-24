package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellHistoryAdapter adapter;
    private List<SoldItem> soldItems;
    ImageView btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sell_history);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.sellHistoryRecyclerView);
        progressBar = findViewById(R.id.progressBar); // link to XML

        // Back button finishes activity
        btnBack.setOnClickListener(view -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        soldItems = new ArrayList<>();
        adapter = new SellHistoryAdapter(soldItems);
        recyclerView.setAdapter(adapter);

        // Get current logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Reference to the user's "sold" items
            DatabaseReference soldRef = FirebaseDatabase.getInstance().getReference("sold").child(userId);

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Listen for sold data
            soldRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    soldItems.clear(); // clear old data

                    int total = (int) snapshot.getChildrenCount(); // total items
                    if (total == 0) {
                        progressBar.setVisibility(View.GONE);
                    }

                    final int[] loadedCount = {0}; // track how many loaded

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        SoldItem item = snap.getValue(SoldItem.class);
                        if (item != null && item.getPostId() != null) {
                            String postId = item.getPostId();

                            // Reference to the original post to get the title
                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                    // Fetch title or default
                                    if (postSnapshot.exists()) {
                                        String postTitle = postSnapshot.child("title").getValue(String.class);
                                        item.setPostTitle(postTitle != null ? postTitle : "No Title Found");
                                    } else {
                                        item.setPostTitle("Post not found");
                                    }

                                    soldItems.add(item);
                                    loadedCount[0]++;

                                    // Update UI when all items are loaded
                                    if (loadedCount[0] == total) {
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE); // hide progress bar
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    item.setPostTitle("Error loading title");
                                    soldItems.add(item);
                                    loadedCount[0]++;

                                    if (loadedCount[0] == total) {
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SellHistoryActivity.this, "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}