package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    TextView userName, userCnic, userPhone, userAddress, userLicenseNumber, userlicenseText;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = findViewById(R.id.textViewUserName);
        userAddress = findViewById(R.id.textViewUserAddress);
        userCnic = findViewById(R.id.textViewUserCnic);
        userPhone = findViewById(R.id.textViewUserPhone);
        userImage = findViewById(R.id.imageViewUser);
        userLicenseNumber = findViewById(R.id.textViewUserLicenseNumber);
        userlicenseText = findViewById(R.id.licenseText);
        setUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserData();
    }

    private void setUserData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Initializing, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Picasso.get().load(user.getPhotoUrl()).into(userImage);
        FirebaseDatabase.getInstance().getReference().child("usersData")
                .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAddress.setText(snapshot.child("address").getValue(String.class));
                userPhone.setText(snapshot.child("phone").getValue(String.class));
                userCnic.setText(snapshot.child("cnic").getValue(String.class));
                userName.setText(snapshot.child("name").getValue(String.class));
                if (Objects.equals(snapshot.child("type").getValue(String.class), "admin")) {
                    userLicenseNumber.setVisibility(View.GONE);
                    userlicenseText.setVisibility(View.GONE);
                    progressDialog.dismiss();
                } else {
                    if (Objects.equals(snapshot.child("licenseNumber").getValue(String.class), "null")) {
                        userLicenseNumber.setText("Not yet generated Please apply for it .");
                        progressDialog.dismiss();
                        return;
                    }
                    userLicenseNumber.setText(snapshot.child("licenseNumber").getValue(String.class));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });


    }

    public void onEditProfileClicked(View view) {
        Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
        intent.putExtra("address", userAddress.getText().toString());
        intent.putExtra("phone", userPhone.getText().toString());
        intent.putExtra("name", userName.getText().toString());


        startActivity(intent);
    }
}