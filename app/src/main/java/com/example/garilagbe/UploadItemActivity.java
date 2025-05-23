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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    EditText editTitle, editDescription, editPrice, editLocation, editMileage, editContact;
    Spinner spinnerType, spinnerFuelType;

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
        editMileage = findViewById(R.id.edit_mileage);
        editContact = findViewById(R.id.edit_contact);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerFuelType = findViewById(R.id.spinner_fuel_type);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("posts");

        // Setup vehicle type spinner
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        // Setup fuel type spinner
        String[] fuelTypes = {"CNG", "Octane", "Petrol", "Hybrid"};
        ArrayAdapter<String> adapterFuel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fuelTypes);
        adapterFuel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapterFuel);

        // Image selection
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Submit form
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                String base64 = encodeImageToBase64(selectedBitmap);
                if (base64 != null) {
                    savePostToDB(base64);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                long fileSize = getContentResolver().openAssetFileDescriptor(imageUri, "r").getLength();
                if (fileSize > 1048576) { // 1MB in bytes
                    Toast.makeText(this, "Image size must be less than 1MB.", Toast.LENGTH_LONG).show();
                    selectedBitmap = null;
                    imagePreview.setImageDrawable(null);
                    return;
                }

                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(selectedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (editTitle.getText().toString().isEmpty() ||
                editDescription.getText().toString().isEmpty() ||
                editPrice.getText().toString().isEmpty() ||
                editLocation.getText().toString().isEmpty() ||
                editMileage.getText().toString().isEmpty() ||
                editContact.getText().toString().isEmpty() ||
                selectedBitmap == null) {

            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void savePostToDB(String imageBase64) {
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        String price = editPrice.getText().toString();
        String location = editLocation.getText().toString();
        String mileage = editMileage.getText().toString();
        String contact = editContact.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        String fuelType = spinnerFuelType.getSelectedItem().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String postId = dbRef.push().getKey();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("userId", userId);
        postMap.put("imageBase64", imageBase64);
        postMap.put("type", type);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("fuelType", fuelType);
        postMap.put("mileage", mileage);
        postMap.put("price", price);
        postMap.put("contact", contact);
        postMap.put("location", location);
        postMap.put("date", currentDate);
        postMap.put("time", currentTime);

        dbRef.child(postId).setValue(postMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent = type.equals("Car")
                            ? new Intent(UploadItemActivity.this, CarViewActivity.class)
                            : new Intent(UploadItemActivity.this, BikeViewActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
