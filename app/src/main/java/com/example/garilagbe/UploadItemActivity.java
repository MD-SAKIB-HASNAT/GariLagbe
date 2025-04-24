package com.example.garilagbe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class UploadItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView imagePreview;
    Button btnSelectImage, btnSubmit;
    EditText editTitle, editDescription, editPrice, editLocation;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);

        imagePreview = findViewById(R.id.image_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSubmit = findViewById(R.id.btn_submit);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editPrice = findViewById(R.id.edit_price);
        editLocation = findViewById(R.id.edit_location);

        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnSubmit.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String description = editDescription.getText().toString();
            String price = editPrice.getText().toString();
            String location = editLocation.getText().toString();

            if (title.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // You can upload this data to Firebase or your server here
            Toast.makeText(this, "Item Submitted!", Toast.LENGTH_SHORT).show();
        });
    }


    // Open gallery to choose image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //  Get image from result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
