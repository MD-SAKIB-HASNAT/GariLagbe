package com.example.garilagbe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garilagbe.R;
import com.example.garilagbe.SoldItem;

import java.util.List;

public class SellHistoryAdapter extends RecyclerView.Adapter<SellHistoryAdapter.ViewHolder> {

    private List<SoldItem> soldItems;

    public SellHistoryAdapter(List<SoldItem> soldItems) {
        this.soldItems = soldItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, buyerName, buyerAddress, buyerPhone, date, time;

        public ViewHolder(View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.txtPostTitle);
            buyerName = itemView.findViewById(R.id.txtBuyerName);
            buyerAddress = itemView.findViewById(R.id.txtBuyerAddress);
            buyerPhone = itemView.findViewById(R.id.txtBuyerPhone);
            date = itemView.findViewById(R.id.txtDate);
            time = itemView.findViewById(R.id.txtTime);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sold_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoldItem item = soldItems.get(position);
        holder.postTitle.setText("Post Title: " + item.getPostTitle());
        holder.buyerName.setText("Buyer: " + item.getBuyerName());
        holder.buyerAddress.setText("Address: " + item.getBuyerAddress());
        holder.buyerPhone.setText("Phone: " + item.getBuyerPhone());
        holder.date.setText("Date: " + item.getDate());
        holder.time.setText("Time: " + item.getTime());
    }

    @Override
    public int getItemCount() {
        return soldItems.size();
    }
}
