package com.example.garilagbe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    // UI components
    private TextView titleTxt, descriptionTxt, fuelType, milageRanTxt, contactBuyerTxt, priceTxt, locationTxt, dateTxt, timeTxt;
    private TextView ratingCountTxt; // TextView to show favorite count
    private ImageView vehicleImage, imgFav, backBtn;
    private Button seeMore;

    // Firebase references
    private DatabaseReference favoriteRef; // Ref for current user's favorites
    private DatabaseReference globalFavoriteRef; // Ref for global favorites
    private String userId;

    private Post post;
    private boolean isFavorite = false; // To track if current user favorited this post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        fuelType = findViewById(R.id.fuel_type);
        milageRanTxt = findViewById(R.id.milageRanTxt);
        contactBuyerTxt = findViewById(R.id.contactBuyerTxt);
        priceTxt = findViewById(R.id.priceTxt);
        vehicleImage = findViewById(R.id.v_img);
        seeMore = findViewById(R.id.see_more);
        backBtn = findViewById(R.id.btn_back);
        imgFav = findViewById(R.id.img_fav);
        locationTxt = findViewById(R.id.location);
        timeTxt = findViewById(R.id.time);
        dateTxt = findViewById(R.id.date);
        ratingCountTxt = findViewById(R.id.rating_count); // Bind rating count TextView

        // Get current user
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            favoriteRef = FirebaseDatabase.getInstance().getReference("favorite").child(userId);
            globalFavoriteRef = FirebaseDatabase.getInstance().getReference("favorite"); // All users' favorites
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity
            return;
        }

        // Back button click
        backBtn.setOnClickListener(view -> finish());

        // "See More" button click
        seeMore.setOnClickListener(view -> finish());

        // Get Post object from Intent
        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Post data not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Validate post ID
        if (post.getPostId() == null || post.getPostId().isEmpty()) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate data into UI
        titleTxt.setText(post.getTitle());
        descriptionTxt.setText(post.getDescription());
        fuelType.setText(post.getFuelType());
        milageRanTxt.setText(post.getMileage() + " km");
        contactBuyerTxt.setText(post.getContact());
        priceTxt.setText(post.getPrice() + "/=");
        locationTxt.setText("Location : " + post.getLocation());
        timeTxt.setText("Post Time : " + post.getTime());
        dateTxt.setText("Post Date : " + post.getDate());

        // Decode and show image from Base64
        if (post.getImageBase64() != null && !post.getImageBase64().isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                vehicleImage.setImageBitmap(decodedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if current user has favorited this post
        checkIfFavorite();

        // Load total favorite count for this post
        loadFavoriteCount();

        // Favorite button click
        imgFav.setOnClickListener(view -> toggleFavorite());
    }

    /**
     * Check if the current user has favorited this post
     */
    private void checkIfFavorite() {
        favoriteRef.child(post.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFavorite = true;
                    imgFav.setImageResource(R.drawable.favoriteselect);
                } else {
                    isFavorite = false;
                    imgFav.setImageResource(R.drawable.favorite);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Error checking favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load total favorite count for this post from "favorite" node
     */
    private void loadFavoriteCount() {
        globalFavoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.hasChild(post.getPostId())) {
                        count++;
                    }
                }
                ratingCountTxt.setText(String.valueOf(count)); // Update the count in UI
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Error loading favorite count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Toggle favorite status when user clicks the favorite icon
     */
    private void toggleFavorite() {
        if (isFavorite) {
            // Remove from favorites
            favoriteRef.child(post.getPostId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isFavorite = false;
                    imgFav.setImageResource(R.drawable.favorite);
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    loadFavoriteCount(); // Refresh count
                } else {
                    Toast.makeText(this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add to favorites
            favoriteRef.child(post.getPostId()).setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isFavorite = true;
                    imgFav.setImageResource(R.drawable.favoriteselect);
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    loadFavoriteCount(); // Refresh count
                } else {
                    Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
