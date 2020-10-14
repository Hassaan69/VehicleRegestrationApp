package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddVehicleDetails extends AppCompatActivity {
    private Spinner carType;
    private TextView textView;
    private Button button, imageSelectButton;
    private ImageView carImage , carImageBig;
    private ProgressDialog progressDialog;

    private EditText carName, carColor, carModelNo, carRegistrationNumber, carChasisNumber, carEngineCapacity, carOwnerName;
    String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_details);
        carChasisNumber = findViewById(R.id.carChasisNumber);
        carColor = findViewById(R.id.carColor);
        carEngineCapacity = findViewById(R.id.carEngineCapacity);
        carModelNo = findViewById(R.id.carModelNumber);
        carRegistrationNumber = findViewById(R.id.carRegistrationNumber);
        carOwnerName = findViewById(R.id.carOwnerName);
        carName = findViewById(R.id.carName);
        carType = findViewById(R.id.cartype);
        button = findViewById(R.id.submitCarDetails);
        textView = findViewById(R.id.textView);
        imageSelectButton = findViewById(R.id.uploadImageButton);
        carImage = findViewById(R.id.carImage);
        carImageBig = findViewById(R.id.carImageBig);

        carImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carImage.getDrawable() == null) {
                        return;
                }
                carImageBig.setVisibility(View.VISIBLE);
                carImageBig.setImageDrawable(carImage.getDrawable());

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        CarModel carModel = (CarModel) getIntent().getSerializableExtra("userDetails");
        if (carModel != null) {
            textView.setVisibility(View.GONE);
            carType.setVisibility(View.GONE);
            button.setText("Edit");
            carChasisNumber.setText(carModel.getChasisNumber());
            carEngineCapacity.setText(carModel.getEngineCapacity());
            carName.setText(carModel.getCarName());
            carModelNo.setText(carModel.getCarModel());
            carOwnerName.setText(carModel.getOwnerName());
            carRegistrationNumber.setText(carModel.getRegistrationNumber());
            carColor.setText(carModel.getColor());
            Picasso.get().load(carModel.getImage()).into(carImage);
            imageSelectButton.setEnabled(false);

            carName.setEnabled(false);

            carColor.setEnabled(false);

            carRegistrationNumber.setEnabled(false);

            carOwnerName.setEnabled(false);

            carModelNo.setEnabled(false);

            carEngineCapacity.setEnabled(false);

            carChasisNumber.setEnabled(false);


        }
    }

    public void onSubmitCarDetails(View view) {
        if (button.getText().toString().equals("Edit")) {
            textView.setText("You can edit your car details here");
            textView.setVisibility(View.VISIBLE);
            carName.setClickable(false);
            carName.setFocusable(false);
            button.setText("Update");

            carColor.setEnabled(true);

            carRegistrationNumber.setEnabled(true);

            carOwnerName.setEnabled(true);

            carModelNo.setEnabled(true);

            carEngineCapacity.setEnabled(true);

            carChasisNumber.setEnabled(true);

            imageSelectButton.setEnabled(true);

            return;
        }
        if (button.getText().toString().equals("Update")) {
            if (getIntent().getStringExtra("uid") == null) {

                SharedPreferences sp1 = getSharedPreferences("LoginCredientials", 0);
                uid = sp1.getString("uid", "none");
            } else {
                uid = getIntent().getStringExtra("uid");
            }
            if (TextUtils.isEmpty(carChasisNumber.getText().toString()) || TextUtils.isEmpty(carColor.getText().toString().trim().toLowerCase())
                    || TextUtils.isEmpty(carEngineCapacity.getText().toString()) || TextUtils.isEmpty(carModelNo.getText().toString()) ||
                    TextUtils.isEmpty(carRegistrationNumber.getText().toString()) || TextUtils.isEmpty(carOwnerName.getText().toString().toLowerCase()) ||
                    TextUtils.isEmpty(carName.getText().toString().toLowerCase())) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("chasisNumber", carChasisNumber.getText().toString());
            map.put("color", carColor.getText().toString());
            map.put("engineCapacity", carEngineCapacity.getText().toString());
            map.put("carModel", carModelNo.getText().toString());
            map.put("registrationNumber", carRegistrationNumber.getText().toString());
            map.put("ownerName", carOwnerName.getText().toString());
            map.put("carName", carName.getText().toString());

            FirebaseDatabase.getInstance().getReference().child("carDetails")
                    .child(uid).child(carName.getText().toString())
                    .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddVehicleDetails.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    finish();
                    button.setText("Submit");
                }
            });
            return;
        }
        SharedPreferences sp1 = getSharedPreferences("LoginCredientials", 0);
        String uid = sp1.getString("uid", "none");
        if (TextUtils.isEmpty(carChasisNumber.getText().toString()) || TextUtils.isEmpty(carColor.getText().toString().trim().toLowerCase())
                || TextUtils.isEmpty(carEngineCapacity.getText().toString()) || TextUtils.isEmpty(carModelNo.getText().toString()) ||
                TextUtils.isEmpty(carRegistrationNumber.getText().toString()) || TextUtils.isEmpty(carOwnerName.getText().toString().toLowerCase()) ||
                TextUtils.isEmpty(carName.getText().toString().toLowerCase())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("chasisNumber", carChasisNumber.getText().toString());
        map.put("color", carColor.getText().toString());
        map.put("engineCapacity", carEngineCapacity.getText().toString());
        map.put("carModel", carModelNo.getText().toString());
        map.put("registrationNumber", carRegistrationNumber.getText().toString());
        map.put("ownerName", carOwnerName.getText().toString());
        map.put("carName", carName.getText().toString());
        map.put("carType", carType.getSelectedItem().toString());

        FirebaseDatabase.getInstance().getReference().child("carDetails")
                .child(uid)
                .child(carName.getText().toString())
                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddVehicleDetails.this, "Details Added", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });
    }

    public void onImageSelectClicked(View view) {

        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).subscribe(granted -> {

            if (granted) {
                if (TextUtils.isEmpty(carChasisNumber.getText().toString()) || TextUtils.isEmpty(carColor.getText().toString().trim().toLowerCase())
                        || TextUtils.isEmpty(carEngineCapacity.getText().toString()) || TextUtils.isEmpty(carModelNo.getText().toString()) ||
                        TextUtils.isEmpty(carRegistrationNumber.getText().toString()) || TextUtils.isEmpty(carOwnerName.getText().toString().toLowerCase()) ||
                        TextUtils.isEmpty(carName.getText().toString().toLowerCase())) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadCarImage();
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri filePath = result.getUri();
            uploadImage(filePath);
        }
    }

    void uploadImage(Uri filePath) {
        if (getIntent().getStringExtra("uid") == null) {

            SharedPreferences sp1 = getSharedPreferences("LoginCredientials", 0);
            uid = sp1.getString("uid", "none");
        } else {
            uid = getIntent().getStringExtra("uid");
        }
        if (filePath != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating profile image...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("Carimages")
                    .child(UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                FirebaseDatabase.getInstance().getReference().child("carDetails")
                                        .child(uid).child(carName.getText().toString().trim())
                                        .child("image")
                                        .setValue(uri.toString())
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Picasso.get().load(uri).into(carImage);
                                                progressDialog.dismiss();
                                            } else {
                                                Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Submit Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });
        }
    }


    void uploadCarImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .start(this);
    }

    @Override
    public void onBackPressed() {
        if (carImageBig.getVisibility() == View.VISIBLE)
        {
            carImageBig.setVisibility(View.GONE);

        }
        else
        {
            super.onBackPressed();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (button.getText().toString().equals("Edit")) {
            textView.setText("You can edit your car details here");
            textView.setVisibility(View.VISIBLE);
            carName.setClickable(false);
            carName.setFocusable(false);
            button.setText("Update");

            carColor.setEnabled(true);

            carRegistrationNumber.setEnabled(true);

            carOwnerName.setEnabled(true);

            carModelNo.setEnabled(true);

            carEngineCapacity.setEnabled(true);

            carChasisNumber.setEnabled(true);

            imageSelectButton.setEnabled(true);

        }
    }
}