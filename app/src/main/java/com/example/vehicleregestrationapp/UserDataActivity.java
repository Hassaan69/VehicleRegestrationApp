package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDataActivity extends AppCompatActivity {
    private EditText userAddress, userPhone, userCnic, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        userAddress = findViewById(R.id.editTextTextPostalAddress);
        userPhone = findViewById(R.id.editTextPhone);
        userCnic = findViewById(R.id.editTextCnicNumber);
        userName = findViewById(R.id.editTextUserName);
        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserData();

    }

    private void checkUserData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Initializing, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        FirebaseDatabase.getInstance().getReference().child("usersData")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            SharedPreferences sp = getSharedPreferences("LoginCredientials", 0);
                            SharedPreferences.Editor Ed = sp.edit();
                            Ed.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            Ed.apply();
                            progressDialog.dismiss();
                            startActivity(new Intent(UserDataActivity.this, MainActivity.class));
                            finish();
                        }
                        progressDialog.dismiss();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(dataSnapshot.getKey())) {
//                                startActivity(new Intent(UserDataActivity.this, MainActivity.class));
//                            }
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void onSubmitClicked(View view) {

        if (TextUtils.isEmpty(userAddress.getText().toString().trim()) || TextUtils.isEmpty(userCnic.getText().toString().trim()) || TextUtils.isEmpty(userPhone.getText().toString().trim())) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("phone", userPhone.getText().toString());
            map.put("address", userAddress.getText().toString());
            map.put("cnic", userCnic.getText().toString());
            map.put("type", "individual");
            map.put("name", userName.getText().toString());
            map.put("licenseNumber", "null");
            SharedPreferences sp = getSharedPreferences("LoginCredientials", 0);
            SharedPreferences.Editor Ed = sp.edit();
            Ed.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            Ed.apply();


            FirebaseDatabase.getInstance().getReference().child("usersData")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserDataActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserDataActivity.this, MainActivity.class));
                        finish();

                    }
                }
            });
        }
    }
}