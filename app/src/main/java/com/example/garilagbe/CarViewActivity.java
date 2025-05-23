package com.example.garilagbe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    RecyclerView carRecyclerView;
    VehicleAdapter vehicleAdapter;
    List<Post> carPostList; // Dynamic list to store posts

    ProgressBar progressBar;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize RecyclerView
        carRecyclerView = findViewById(R.id.car_recview);
        progressBar = findViewById(R.id.progressBarCar);
        ImageView filterIcon = findViewById(R.id.img_filter);
        filterIcon.setOnClickListener(v -> showFilterDialog());
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carPostList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(carPostList,CarViewActivity.this); // pass dynamic data
        carRecyclerView.setAdapter(vehicleAdapter);
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



    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_filter_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        RangeSlider priceSlider = view.findViewById(R.id.sliderPrice);
        TextView priceRangeText = view.findViewById(R.id.priceRangeText);
        Button btnApply = view.findViewById(R.id.btnApplyFilter);
        // Show current price range on slider change
        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = priceSlider.getValues();
            priceRangeText.setText((float) values.get(0) + " - " + (float) values.get(1) + " Tk");
        });

        btnApply.setOnClickListener(v -> {
            int minPrice = Math.round(priceSlider.getValues().get(0));
            int maxPrice = Math.round(priceSlider.getValues().get(1));
           // applyFilter(minPrice, maxPrice);
            dialog.dismiss();
        });

        dialog.show();
    }




/*    private void applyFilter(int minPrice, int maxPrice) {
        List<CarModel> filteredList = new ArrayList<>();


        for (CarModel car : fullCarList) {
            try {
                if (car.getPrice() >= minPrice && car.getPrice() <= maxPrice) {
                    filteredList.add(car);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        carAdapter.setData(filteredList);
    }*/

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
