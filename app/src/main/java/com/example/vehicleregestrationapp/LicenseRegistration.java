package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LicenseRegistration extends AppCompatActivity {

    private Spinner spinner1, spinner2, spinner3, spinner4, spinner5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_registration);
        spinner1 = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);


    }

    public void onLicenseSubmitClicked(View view) {
        if (spinner1.getSelectedItem().toString().equals("Select Answer") || spinner2.getSelectedItem().toString().equals("Select Answer")
                || spinner3.getSelectedItem().toString().equals("Select Answer") || spinner4.getSelectedItem().toString().equals("Select Answer")
                || spinner5.getSelectedItem().toString().equals("Select Answer")) {
            Toast.makeText(this, "Please Enter All Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinner1.getSelectedItem().toString().equals("Non of these")
                || spinner2.getSelectedItem().toString().equals("Mandatory")
                || spinner3.getSelectedItem().toString().equals("All of the above")
                || spinner4.getSelectedItem().toString().equals("40 kms")
                || spinner5.getSelectedItem().toString().equals("All of the above"))
        {
            Map<String,Object> map = new HashMap<>();
            final String licenseNumber = UUID.randomUUID().toString();
            map.put("licenseNumber",licenseNumber);

            FirebaseDatabase.getInstance().getReference().child("usersData")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(LicenseRegistration.this, "License Number Generated " + licenseNumber , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            });
        } else if (spinner1.getSelectedItem().toString().equals("Non of these")
                || spinner2.getSelectedItem().toString().equals("Mandatory")
                || spinner3.getSelectedItem().toString().equals("All of the above")
                || spinner4.getSelectedItem().toString().equals("40 kms"))
        {
            Map<String,Object> map = new HashMap<>();
            final String licenseNumber = UUID.randomUUID().toString();
            map.put("licenseNumber",licenseNumber);

            FirebaseDatabase.getInstance().getReference().child("usersData")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(LicenseRegistration.this, "License Number Generated " + licenseNumber , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            });

        }else
        {
            Toast.makeText(this, "Please Select Correct Answers", Toast.LENGTH_SHORT).show();
        }
    }
}