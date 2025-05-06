package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarViewActivity extends AppCompatActivity {

    /*private RecyclerView categoryRecyclerView, carRecyclerView;
    private CategoryAdapter categoryAdapter;
    private CarAdapter carAdapter;
    private final List<CategoryModel> categoryList = new ArrayList<>();
    private final List<CarModel> carList = new ArrayList<>();
    private final List<CarModel> fullCarList = new ArrayList<>(); // backup for filtering

    private ProgressBar progressBar;

    private final String API_URL = "http://your-api-url.com/api/car-data"; // Change this to actual API endpoint
*/

    RecyclerView carRecyclerView;
    RecyclerView brandRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_view);
        ImageView cartBtn = findViewById(R.id.btn_cart);

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarViewActivity.this, DetailActivity.class));
            }
        });

        CarAdapter carAdapter = new CarAdapter();
        carRecyclerView = findViewById(R.id.car_recview);
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carRecyclerView.setAdapter(carAdapter);



        /*categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        carRecyclerView = findViewById(R.id.carRecyclerView);
        progressBar = findViewById(R.id.progressBarCar); // progressBarCategory now didnt take for simplicity

        ImageView homeBtn = findViewById(R.id.btn_home);//bottom navigation panel home button

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarViewActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish(); // Optional: prevents user from going back to CarViewActivity when pressing back
            }
        });

        // Set up RecyclerViews
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        carAdapter = new CarAdapter(this, carList);

        categoryRecyclerView.setAdapter(categoryAdapter);
        carRecyclerView.setAdapter(carAdapter);

        fetchData();*/
    }


    /*private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        if (response.getInt("status") == 200) {
                            JSONArray categories = response.getJSONArray("Category");
                            JSONArray cars = response.getJSONArray("Cars");

                            categoryList.clear();
                            carList.clear();
                            fullCarList.clear();

                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject cat = categories.getJSONObject(i);
                                categoryList.add(new CategoryModel(
                                        cat.getInt("id"),
                                        cat.getString("picName"),
                                        cat.getString("title")
                                ));
                            }

                            for (int i = 0; i < cars.length(); i++) {
                                JSONObject car = cars.getJSONObject(i);
                                CarModel model = new CarModel(
                                        car.getString("title"),
                                        car.getString("picName"),
                                        car.getString("contactBuyer"),
                                        car.getString("milageRan"),
                                        car.getInt("price"),
                                        (float) car.getDouble("rating"),
                                        car.getString("TotalCapacity"),
                                        car.getString("description")
                                );
                                carList.add(model);
                                fullCarList.add(model);
                            }

                            categoryAdapter.notifyDataSetChanged();
                            carAdapter.notifyDataSetChanged();
                        }

                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", error.toString());
                    progressBar.setVisibility(View.GONE);
                });

        queue.add(jsonRequest);
    }

    // When a category is selected
    @Override
    public void onCategoryClick(int categoryId) {
        List<CarModel> filteredList = new ArrayList<>();

        // For simplicity, just filter by index (position of category)
        // later will be implement a backend-side filter if needed
        for (int i = 0; i < fullCarList.size(); i++) {
            if (i % categoryList.size() == categoryId) { // Fake match just for filtering purpose now
                filteredList.add(fullCarList.get(i));
            }
        }

        carAdapter.updateList(filteredList);
    }*/
}