package com.example.garilagbe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UploadItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI components
    ImageView imagePreview;
    Button btnSelectImage, btnSubmit;
    EditText editTitle, editDescription, editPrice, editLocation;
    Spinner spinnerType;

    // Image-related
    Uri imageUri;
    Bitmap selectedBitmap;

    // Firebase
    FirebaseDatabase database;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);

        // Initialize views
        imagePreview = findViewById(R.id.image_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSubmit = findViewById(R.id.btn_submit);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editPrice = findViewById(R.id.edit_price);
        editLocation = findViewById(R.id.edit_location);
        spinnerType = findViewById(R.id.spinner_type);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("posts");

        // Set up spinner with vehicle types (e.g., Car, Bike)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // When select image button is clicked
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // When submit button is clicked
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                // If inputs are valid, encode image and save post
                savePostToDB(encodeImageToBase64(selectedBitmap));
            }
        });
    }

    // Open the gallery to pick an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Convert URI to Bitmap
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(selectedBitmap); // Show preview
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Validate all user inputs
    private boolean validateInputs() {
        if (editTitle.getText().toString().isEmpty() ||
                editDescription.getText().toString().isEmpty() ||
                editPrice.getText().toString().isEmpty() ||
                editLocation.getText().toString().isEmpty() ||
                selectedBitmap == null) {

            Toast.makeText(this, "Fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Convert Bitmap to Base64 string
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Compress image to reduce size (50% quality)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        // Convert to Base64 string
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Save post to Firebase Realtime Database
    private void savePostToDB(String imageBase64) {
        // Get all input values
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        String price = editPrice.getText().toString();
        String location = editLocation.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String postId = dbRef.push().getKey(); // Unique post ID

        // Create a HashMap for post data
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("userId", userId);
        postMap.put("imageBase64", imageBase64);
        postMap.put("type", type);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("price", price);
        postMap.put("location", location);

        // Save data under /posts/postId in Firebase
        dbRef.child(postId).setValue(postMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    if(type.equals("Car")) {
                         intent = new Intent(UploadItemActivity.this, CarViewActivity.class);
                    }
                    else{
                        intent = new Intent(UploadItemActivity.this, BikeViewActivity.class);
                    }
                    startActivity(intent);
                    finish(); // Close current activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
