package com.example.vehicleregestrationapp;

import android.content.Context;
import android.content.Intent;
import android.service.autofill.UserData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.MyViewHolder> implements Filterable {
    private List<UserModel> userList;
    private List<UserModel> userListFull;

    private Context context;

    public UserDataAdapter(List<UserModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
        userListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyc_user_item, parent, false);

        return new UserDataAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final UserModel userData = userList.get(position);
        holder.userName.setText(userData.getName());
        holder.userAddress.setText(userData.getAddress());
        holder.userPhone.setText(userData.getPhone());
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_user));
        holder.userCnic.setText(userData.getCnic());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UserInformation.class);
                intent.putExtra("userInfo",userData);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<UserModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(userListFull);
            } else {
                String filterpattern = charSequence.toString().toLowerCase().trim();
                for (UserModel item : userListFull) {
                    if (item.getName().toLowerCase().contains(filterpattern)) {
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
            userList.clear();
            userList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, userPhone, userAddress , userCnic;
        private ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.rvUserName);
            userPhone = view.findViewById(R.id.rvUserPhone);
            userAddress = view.findViewById(R.id.rvUserAddress);
            imageView = view.findViewById(R.id.rvimageView);
            userCnic = view.findViewById(R.id.rvUserCnic);

        }
    }
}
