package com.example.garilagbe;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RentPage extends AppCompatActivity {
    RecyclerView rentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rent_page);

        RentAdapter rentAdapter = new RentAdapter();

        rentView = findViewById(R.id.rent_view);
        rentView.setLayoutManager(new LinearLayoutManager(this));
        rentView.setAdapter(rentAdapter);

    }
}