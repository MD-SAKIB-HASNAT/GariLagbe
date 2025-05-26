package com.example.garilagbe;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    RecyclerView carRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> carPostList = new ArrayList<>(); // Filtered list of car posts
    List<Post> fullCarPostList = new ArrayList<>(); // Full list of car posts (for filtering)

    EditText editTextSearch;
    ListView suggestionListView;
    ArrayAdapter<String> suggestionAdapter;
    List<String> suggestions = new ArrayList<>(); // Suggestions for search

    ProgressBar progressBar;
    ImageView btnBack, btnPost, btnHome, profileImg, btnFav, btnProfile;
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize UI components
        editTextSearch = findViewById(R.id.editTextSearch);
        suggestionListView = findViewById(R.id.suggestionListView);

        carRecyclerView = findViewById(R.id.car_recview);
        progressBar = findViewById(R.id.progressBarCar);
        ImageView filterIcon = findViewById(R.id.img_filter);
        filterIcon.setOnClickListener(v -> showFilterDialog()); // Show filter dialog when filter icon is clicked

        // Setup RecyclerView with grid layout
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        vehicleAdapter = new VehicleAdapter(carPostList, CarViewActivity.this);
        carRecyclerView.setAdapter(vehicleAdapter);

        // Initialize navigation buttons
        btnBack = findViewById(R.id.btn_back);
        btnPost = findViewById(R.id.btn_post);
        btnHome = findViewById(R.id.btn_home);
        btnProfile = findViewById(R.id.btnProfile);
        btnFav = findViewById(R.id.btn_fav);

        // Navigation button listeners
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CarViewActivity.this, MainHome.class);
            intent.putExtra("showFragment", "profile"); // Switch to profile fragment
            startActivity(intent);
        });

        btnFav.setOnClickListener(v -> startActivity(new Intent(CarViewActivity.this, FavoritePostActivity.class)));

        btnPost.setOnClickListener(view -> {
            finish(); // Close current activity
            startActivity(new Intent(CarViewActivity.this, UploadItemActivity.class)); // Open upload item activity
        });

        btnBack.setOnClickListener(view -> finish());
        btnHome.setOnClickListener(view -> finish());

        // User profile components
        profileName = findViewById(R.id.profile_name);
        profileImg = findViewById(R.id.user_profile);

        // Load user's profile image and name
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadUserProfile(uid);
        loadUserName(uid);

        // Load car posts from Firebase
        loadPostsFromFirebase();

        // Setup search suggestion adapter
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        suggestionListView.setAdapter(suggestionAdapter);

        // Listen for search input changes
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterModelName(s.toString()); // Filter posts based on search query
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Handle suggestion selection
        suggestionListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = suggestionAdapter.getItem(position);
            editTextSearch.setText(selected);
            suggestionListView.setVisibility(View.GONE); // Hide suggestion list
            filterModelName(selected); // Filter posts
        });
    }

    // Replace a fragment (optional; currently unused in this activity)
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Filter posts based on search query (title)
    private void filterModelName(String query) {
        List<Post> filteredList = new ArrayList<>();
        List<String> suggestionMatches = new ArrayList<>();

        for (Post post : fullCarPostList) {
            if (post.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(post);
                if (!suggestionMatches.contains(post.getTitle())) {
                    suggestionMatches.add(post.getTitle());
                }
            }
        }

        vehicleAdapter.setFilteredList(filteredList);

        if (!query.isEmpty() && !suggestionMatches.isEmpty()) {
            suggestionAdapter.clear();
            suggestionAdapter.addAll(suggestionMatches);
            suggestionAdapter.notifyDataSetChanged();
            suggestionListView.setVisibility(View.VISIBLE); // Show suggestions
        } else {
            suggestionListView.setVisibility(View.GONE); // Hide suggestions
        }
    }

    // Show filter dialog for selecting price range
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_filter_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        RangeSlider priceSlider = view.findViewById(R.id.sliderPrice);
        TextView priceRangeText = view.findViewById(R.id.priceRangeText);
        Button btnApply = view.findViewById(R.id.btnApplyFilter);

        priceSlider.setValues(0f, 20000000f);
        priceSlider.setStepSize(10000f);
        priceRangeText.setText("0 - 20000000 Tk");

        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = priceSlider.getValues();
            int min = Math.round(values.get(0));
            int max = Math.round(values.get(1));
            priceRangeText.setText(min + " - " + max + " Tk");
        });

        btnApply.setOnClickListener(v -> {
            int minPrice = Math.round(priceSlider.getValues().get(0));
            int maxPrice = Math.round(priceSlider.getValues().get(1));
            applyPriceFilter(minPrice, maxPrice); // Filter posts by price
            dialog.dismiss();
        });

        dialog.show();
    }

    // Apply filter to posts based on selected price range
    private void applyPriceFilter(int minPrice, int maxPrice) {
        List<Post> filteredList = new ArrayList<>();
        for (Post post : fullCarPostList) {
            try {
                int price = Integer.parseInt(post.getPrice());
                if (price >= minPrice && price <= maxPrice) {
                    filteredList.add(post);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Handle invalid price format
            }
        }
        vehicleAdapter.setFilteredList(filteredList);
    }

    // Load car posts from Firebase
    private void loadPostsFromFirebase() {
        carPostList.clear();
        fullCarPostList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && "Car".equals(post.getType()) && "Available".equals(post.getStatus())) {
                        post.setPostId(dataSnapshot.getKey()); // Save Firebase key as post ID
                        carPostList.add(post);
                        fullCarPostList.add(post);
                    }
                }
                vehicleAdapter.setFilteredList(carPostList); // Show all cars initially
                progressBar.setVisibility(View.INVISIBLE); // Hide loading indicator
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CarViewActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load user's profile image from Firebase and display it
    private void loadUserProfile(String uid) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("userprofile").child(uid);
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String base64Image = snapshot.child("profileImage").getValue(String.class);
                    if (base64Image != null && !base64Image.isEmpty()) {
                        try {
                            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Glide.with(CarViewActivity.this)
                                    .asBitmap()
                                    .load(decodedBytes)
                                    .circleCrop()
                                    .into(profileImg);
                        } catch (Exception e) {
                            profileImg.setImageResource(R.drawable.p1); // Default image
                        }
                    } else {
                        profileImg.setImageResource(R.drawable.p1); // Default image
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Load user's name from Firebase and display it
    private void loadUserName(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    profileName.setText(name);
                }
            }

            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}
