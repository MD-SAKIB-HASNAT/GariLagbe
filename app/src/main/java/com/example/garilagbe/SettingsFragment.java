package com.example.garilagbe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SettingsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImage;
    private EditText editName, editMobile, editAddress, editPostal;
    private String base64Image = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        profileImage = view.findViewById(R.id.edit_profile_image);
        editName = view.findViewById(R.id.edit_name);
        editMobile = view.findViewById(R.id.edit_mobile);
        editAddress = view.findViewById(R.id.edit_address);
        editPostal = view.findViewById(R.id.edit_postal);

        Button btnUpload = view.findViewById(R.id.btn_upload_image);
        Button btnSave = view.findViewById(R.id.btn_save_profile);

        btnUpload.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveProfileToFirebase());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                Glide.with(this).load(bitmap).circleCrop().into(profileImage);
                base64Image = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveProfileToFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> profile = new HashMap<>();
        profile.put("name", editName.getText().toString().trim());
        profile.put("phone", editMobile.getText().toString().trim());
        profile.put("address", editAddress.getText().toString().trim());
        profile.put("postal", editPostal.getText().toString().trim());
        profile.put("profileImage", base64Image);

        FirebaseDatabase.getInstance().getReference("userprofile")
                .child(uid)
                .setValue(profile)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Profile Saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
