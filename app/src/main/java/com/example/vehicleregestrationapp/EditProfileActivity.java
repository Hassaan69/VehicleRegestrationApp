package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    EditText phone , address , name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        phone = findViewById(R.id.updateAddress);
        address = findViewById(R.id.edit_phone_number);
        name = findViewById(R.id.updateName);
        phone.setText(getIntent().getStringExtra("phone"));
        address.setText(getIntent().getStringExtra("address"));
        name.setText(getIntent().getStringExtra("name"));


    }

    public void onUpdate(View view)
    {
        HashMap<String, Object> map = new HashMap<>();
        if (TextUtils.isEmpty(phone.getText().toString().trim()) || TextUtils.isEmpty(address.getText().toString().trim()))
        {
            Toast.makeText(this, "Please fill required fields" +FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
            return;
        }
        map.put("address",address.getText().toString().trim());
        map.put("phone",phone.getText().toString().trim());
        map.put("name",name.getText().toString().trim());

        FirebaseDatabase.getInstance().getReference()
                .child("usersData")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(EditProfileActivity.this, "Profile Data Updated Successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }
}