package com.example.garilagbe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garilagbe.Post;
import com.example.garilagbe.R;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public MyPostsAdapter(List<Post> posts,Context context) {
        this.context = context;
        this.posts = posts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTitle, postPrice;
        Button updateButton, deleteButton;

        public ViewHolder(View view) {
            super(view);
            postImage = view.findViewById(R.id.postImage);
            postTitle = view.findViewById(R.id.postTitle);
            postPrice = view.findViewById(R.id.postPrice);
            updateButton = view.findViewById(R.id.updateButton);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.postTitle.setText(post.getTitle());
        holder.postPrice.setText("৳ " + post.getPrice());

        // Decode Base64 image and set to ImageView
        if (post.getImageBase64() != null) {
            try {
                byte[] imageBytes = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.postImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.postImage.setImageResource(R.drawable.add); // fallback image
            }

        }

        // Delete post
        holder.deleteButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (post.getPostId() != null) {
                            FirebaseDatabase.getInstance().getReference("posts")
                                    .child(post.getPostId())
                                    .removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(context, "Error: Post ID is null", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });



        holder.updateButton.setOnClickListener(v -> {
            // Open update activity or dialog
            Intent intent = new Intent(context, UploadItemActivity.class);
            intent.putExtra("postToEdit", post);
            context.startActivity(intent);
            Toast.makeText(context, "Update: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
