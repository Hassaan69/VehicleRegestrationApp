package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserInformation extends AppCompatActivity {
    private TextView userName, userlicense;
    private RecyclerView recyclerView;
    private CarDataAdapter carDataAdapter;
    private List<CarModel> carList;
    private UserModel userModel;
    private String uid;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        userlicense = findViewById(R.id.infroUserLicenseNumber);
        userName = findViewById(R.id.infoUserName);
        userModel = (UserModel) getIntent().getSerializableExtra("userInfo");
        uid = userModel.getUid();
        userName.setText("Name:" + userModel.getName());
        if (userModel.getLicenseNumber().equals("null")) {
            userlicense.setText("User has been not assigned a License Number");
        } else {
            userlicense.setText("License Number:" + userModel.getLicenseNumber());
        }
        recyclerView = findViewById(R.id.infoRv);
        searchView = findViewById(R.id.searchView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        init();

    }

    private void init() {

        FirebaseDatabase.getInstance().getReference().child("carDetails").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        carList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            carList.add(dataSnapshot.getValue(CarModel.class));
                        }
                        carDataAdapter = new CarDataAdapter(carList, UserInformation.this, uid);
                        recyclerView.setAdapter(carDataAdapter);
                        carDataAdapter.notifyDataSetChanged();
                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                carDataAdapter.getFilter().filter(newText);
                                return false;
                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}