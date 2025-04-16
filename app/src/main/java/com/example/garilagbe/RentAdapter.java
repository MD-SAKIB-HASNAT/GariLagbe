package com.example.garilagbe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.zip.Inflater;

public class RentAdapter extends RecyclerView.Adapter<RentAdapter.RentViewHolder> {
    String comName[]={"OBHAI", "Cholo", "Car Chai", "Rent A Car Bangladesh", "BRB Car Rental", "DriveIt by Bondstein", "Pathao", "Uber Moto", "Garivara", "RideKaru"};
    String comEmail[]={"obhai@gmail.com", "cholo@gmail.com", "carchai@gmail.com", "rentacarbangladesh@gmail.com", "brbcarrental@gmail.com", "driveitbd@gmail.com", "pathao@gmail.com", "ubermoto@gmail.com", "garivara@gmail.com", "ridekaru@gmail.com"};
    String comPhone[]={"16633", "01313-875757", "01742-330044", "01917-630502", "01743-440618", "09639-595959", "09678-100800", "09612-888111", "09611-911911", "01896-510131"};
    int comImg[]={R.drawable.rent1, R.drawable.rent2, R.drawable.rent3, R.drawable.rent4, R.drawable.rent5, R.drawable.rent6, R.drawable.rent7, R.drawable.rent8, R.drawable.rent9, R.drawable.rent10};
    @NonNull
    @Override
    public RentAdapter.RentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_item,parent,false);
        return new RentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentAdapter.RentViewHolder holder, int position) {
        holder.txtName.setText(comName[position]);
        holder.txtEmail.setText(comEmail[position]);
        holder.txtPhone.setText(comPhone[position]);
        holder.imgCom.setImageResource(comImg[position]);
    }

    @Override
    public int getItemCount() {
        return comName.length;
    }

    public class RentViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,txtEmail,txtPhone;
        ImageView imgCom;
        public RentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.com_name);
            txtEmail = itemView.findViewById(R.id.com_email);
            txtPhone = itemView.findViewById(R.id.com_phone);
            imgCom = itemView.findViewById(R.id.com_img);
        }
    }
}
