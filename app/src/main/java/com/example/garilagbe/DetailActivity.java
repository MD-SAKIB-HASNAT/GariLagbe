package com.example.garilagbe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTxt, descriptionTxt, fuelType, milageRanTxt, contactBuyerTxt, priceTxt, locationTxt, dateTxt, timeTxt;
    private ImageView vehicleImage;

    Button seeMore;
       ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Bind views
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        fuelType = findViewById(R.id.fuel_type);
        milageRanTxt = findViewById(R.id.milageRanTxt);
        contactBuyerTxt = findViewById(R.id.contactBuyerTxt);
        priceTxt = findViewById(R.id.priceTxt);
        vehicleImage = findViewById(R.id.v_img);
        seeMore = findViewById(R.id.see_more);
        backBtn = findViewById(R.id.btn_back);
        locationTxt = findViewById(R.id.location);
        timeTxt = findViewById(R.id.time);
        dateTxt = findViewById(R.id.date);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get Post object from Intent
        Post post = (Post) getIntent().getSerializableExtra("post");

        if (post != null) {
            titleTxt.setText(post.getTitle());
            descriptionTxt.setText(post.getDescription());
            fuelType.setText(post.getFuelType());
            milageRanTxt.setText(post.getMileage() + " km");
            contactBuyerTxt.setText(post.getContact());
            priceTxt.setText(post.getPrice() + "/=");
            locationTxt.setText("Location : "+post.getLocation());
            timeTxt.setText("Post Time : "+post.getTime());
            dateTxt.setText("Post Date : "+post.getDate());

            // Convert base64 image string to Bitmap
            if (post.getImageBase64() != null && !post.getImageBase64().isEmpty()) {
                try {
                    byte[] imageBytes = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    vehicleImage.setImageBitmap(decodedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
