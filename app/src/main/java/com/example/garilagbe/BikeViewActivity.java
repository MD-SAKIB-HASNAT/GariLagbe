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

public class BikeViewActivity extends AppCompatActivity {

    RecyclerView bikeRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> bikePostList = new ArrayList<>();
    List<Post> fullBikePostList = new ArrayList<>();

    EditText editTextSearch;
    ListView suggestionListView;
    ArrayAdapter<String> suggestionAdapter;
    List<String> suggestions = new ArrayList<>();

    ProgressBar progressBar;
    ImageView btnBack, btnPost, btnHome, btnFav, btnProfile, profileImg;
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_view);

        editTextSearch = findViewById(R.id.editTextSearch);
        suggestionListView = findViewById(R.id.suggestionListView);

        bikeRecyclerView = findViewById(R.id.bike_recview);
        progressBar = findViewById(R.id.progressBarBike);
        ImageView filterIcon = findViewById(R.id.img_filter);
        filterIcon.setOnClickListener(v -> showFilterDialog());

        bikeRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        vehicleAdapter = new VehicleAdapter(bikePostList, BikeViewActivity.this);
        bikeRecyclerView.setAdapter(vehicleAdapter);

        btnBack = findViewById(R.id.btn_back);
        btnPost = findViewById(R.id.btn_post);
        btnHome = findViewById(R.id.btn_home);
        btnProfile = findViewById(R.id.btnProfile);
        btnFav = findViewById(R.id.btn_fav);
        profileName = findViewById(R.id.profile_name);
        profileImg = findViewById(R.id.user_profile);

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(BikeViewActivity.this, MainHome.class);
            intent.putExtra("showFragment", "profile");
            startActivity(intent);
        });

        btnFav.setOnClickListener(v -> startActivity(new Intent(BikeViewActivity.this, FavoritePostActivity.class)));

        btnPost.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(BikeViewActivity.this, UploadItemActivity.class));
        });

        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> finish());

        // Load user profile and name
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadUserProfile(uid);
        loadUserName(uid);

        // Load bike posts from Firebase
        loadPostsFromFirebase();

        // Setup search suggestion adapter
        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        suggestionListView.setAdapter(suggestionAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterModelName(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        suggestionListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = suggestionAdapter.getItem(position);
            editTextSearch.setText(selected);
            suggestionListView.setVisibility(View.GONE);
            filterModelName(selected);
        });
    }

    private void filterModelName(String query) {
        List<Post> filteredList = new ArrayList<>();
        List<String> suggestionMatches = new ArrayList<>();

        for (Post post : fullBikePostList) {
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
            suggestionListView.setVisibility(View.VISIBLE);
        } else {
            suggestionListView.setVisibility(View.GONE);
        }
    }

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
            applyPriceFilter(minPrice, maxPrice);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyPriceFilter(int minPrice, int maxPrice) {
        List<Post> filteredList = new ArrayList<>();
        for (Post post : fullBikePostList) {
            try {
                int price = Integer.parseInt(post.getPrice());
                if (price >= minPrice && price <= maxPrice) {
                    filteredList.add(post);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        vehicleAdapter.setFilteredList(filteredList);
    }

    private void loadPostsFromFirebase() {
        bikePostList.clear();
        fullBikePostList.clear();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("posts");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Post post = postSnap.getValue(Post.class);
                    if (post != null && "Bike".equals(post.getType()) && "Available".equals(post.getStatus())) {
                        post.setPostId(postSnap.getKey());
                        bikePostList.add(post);
                        fullBikePostList.add(post);
                    }
                }
                vehicleAdapter.setFilteredList(bikePostList);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BikeViewActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile(String uid) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("userprofile").child(uid);
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String base64Image = snapshot.child("profileImage").getValue(String.class);
                    if (base64Image != null && !base64Image.isEmpty()) {
                        try {
                            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Glide.with(BikeViewActivity.this)
                                    .asBitmap()
                                    .load(decodedBytes)
                                    .circleCrop()
                                    .into(profileImg);
                        } catch (Exception e) {
                            profileImg.setImageResource(R.drawable.p1);
                        }
                    } else {
                        profileImg.setImageResource(R.drawable.p1);
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

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
