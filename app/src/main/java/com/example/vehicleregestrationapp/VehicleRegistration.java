package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VehicleRegistration extends AppCompatActivity {
    private ExtendedFloatingActionButton extendedFloatingActionButton;
    private RecyclerView recyclerView;
    private CarDataAdapter carDataAdapter;
    private List<CarModel> carList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);
        extendedFloatingActionButton = findViewById(R.id.fab);
        extendedFloatingActionButton.setText("Add Vechicle");
        extendedFloatingActionButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_add));
        extendedFloatingActionButton.setExtended(true);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        carDataAdapter = new CarDataAdapter(carList, this);
        recyclerView.setAdapter(carDataAdapter);

//        initCarList();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        SharedPreferences sp1 = getSharedPreferences("LoginCredientials", 0);
        String uid = sp1.getString("uid", "none");
        FirebaseDatabase.getInstance().getReference().child("carDetails").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        carList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            carList.add(dataSnapshot.getValue(CarModel.class));
                        }
                        carDataAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void initCarList() {
        carList.clear();
        SharedPreferences sp1 = getSharedPreferences("LoginCredientials", 0);
        String uid = sp1.getString("uid", "none");
        FirebaseDatabase.getInstance().getReference().child("carDetails").child(uid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        CarModel carModel = snapshot.getValue(CarModel.class);
                        carList.add(carModel);
                        carDataAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void onAddVehicleClicked(View view) {
        startActivity(new Intent(VehicleRegistration.this, AddVehicleDetails.class));
    }

}