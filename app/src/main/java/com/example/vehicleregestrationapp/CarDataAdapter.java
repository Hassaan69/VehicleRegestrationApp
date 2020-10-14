package com.example.vehicleregestrationapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CarDataAdapter extends RecyclerView.Adapter<CarDataAdapter.MyViewHolder> implements Filterable {
    private List<CarModel> carList;
    private List<CarModel> carListFull;
    private Context context;
    private String uid = null;

    public CarDataAdapter(List<CarModel> carList, Context context, String uid) {
        this.carList = carList;
        this.context = context;
        this.uid = uid;
        carListFull = new ArrayList<>(carList);
    }

    public CarDataAdapter(List<CarModel> carList, Context context) {
        this.carList = carList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyc_car_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final CarModel carModel = carList.get(position);
        holder.registrationNumber.setText(carModel.getRegistrationNumber());
        holder.carName.setText(carModel.getCarName());
        holder.ownerName.setText(carModel.getOwnerName());
        Picasso.get().load(carModel.getImage()).into(holder.carImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uid == null) {
                    Intent intent = new Intent(context, AddVehicleDetails.class);
                    intent.putExtra("userDetails", carModel);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, AddVehicleDetails.class);
                    intent.putExtra("userDetails", carModel);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<CarModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(carListFull);
            } else {
                String filterpattern = charSequence.toString().toLowerCase().trim();
                for (CarModel item : carListFull) {
                    if (item.getCarName().toLowerCase().contains(filterpattern)) {
                        filteredList.add(item);

                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                        carList.clear();
                        carList.addAll((List)filterResults.values);
                        notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView ownerName, carName, registrationNumber;
        private ImageView carImage;


        public MyViewHolder(View view) {
            super(view);
            ownerName = view.findViewById(R.id.rvOwnerName);
            carName = view.findViewById(R.id.rvCarName);
            registrationNumber = view.findViewById(R.id.rvRegistrationNumber);
            carImage = view.findViewById(R.id.rvCarImage);
        }
    }
}
