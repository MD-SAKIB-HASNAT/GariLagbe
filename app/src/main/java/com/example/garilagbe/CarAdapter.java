package com.example.garilagbe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Post> postList;

    // Constructor receives dynamic post list
    public CarAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate custom layout for item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_list, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Post post = postList.get(position);

        // Decode Base64 image and set to ImageView
        if (post.getImageBase64() != null) {
            byte[] imageBytes = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imgCar.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size(); // return size of dynamic list
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCar;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCar = itemView.findViewById(R.id.carImage);
        }
    }
}
