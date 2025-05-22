package com.example.garilagbe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;


public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.CarBikeViewHolder> {

    private List<Post> postList;

    public VehicleAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public VehicleAdapter.CarBikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_list, parent, false);
        return new CarBikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.CarBikeViewHolder holder, int position) {
        Post post = postList.get(position);

        // Decode Base64 image and set to ImageView
        if (post.getImageBase64() != null) {
            byte[] imageBytes = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imgVehicle.setImageBitmap(bitmap);
        }

        //set title and price
        holder.titleVehicle.setText(post.getTitle());
        holder.priceVehicle.setText(post.getPrice()+" Tk");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class CarBikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVehicle;
        TextView titleVehicle, priceVehicle;
        public CarBikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVehicle = itemView.findViewById(R.id.vehicle_img);
            titleVehicle = itemView.findViewById(R.id.vehicle_title);
            priceVehicle = itemView.findViewById(R.id.vehicle_price);
        }
    }
}
