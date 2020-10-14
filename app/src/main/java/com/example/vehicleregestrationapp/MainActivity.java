package com.example.vehicleregestrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView navUsername;
    private CircleImageView profileimg;
    private Button license, registration;
    private RecyclerView recyclerView;
    private UserDataAdapter userDataAdapter;
    private List<UserModel> userModelList;
    private SearchView searchView;
    private ImageView imageView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.name);
        profileimg = headerView.findViewById(R.id.image);
        imageView = findViewById(R.id.imageView4);
        license = findViewById(R.id.buttonLicense);
        registration = findViewById(R.id.buttonVehicleRegistration);
        recyclerView = findViewById(R.id.recycler_view_main);
        searchView = findViewById(R.id.searchViewMain);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            checkUserType();
            navUsername.setText(user.getDisplayName());
            Picasso.get().load(user.getPhotoUrl()).into(profileimg);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.action_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    }
                });

    }


    public void onVehicleRegistrationClicked(View view) {
        startActivity(new Intent(MainActivity.this, VehicleRegistration.class));
    }

    public void onLicenseClicked(View view) {
        startActivity(new Intent(MainActivity.this, LicenseRegistration.class));
    }

    private void checkUserLicense() {
        FirebaseDatabase.getInstance().getReference().child("usersData")
                .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String licenseNumber = snapshot.child("licenseNumber").getValue(String.class);
                if (!licenseNumber.equals("null")) {
                    license.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserType() {

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Initializing, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        userModelList = new ArrayList<>();
        userModelList.clear();
        Log.d("List Size", "checkUserType: " + userModelList.size());
        FirebaseDatabase.getInstance().getReference().child("usersData")
                .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usertype = snapshot.child("type").getValue(String.class);
                if (usertype.equals("admin")) {
                    license.setVisibility(View.GONE);
                    registration.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                    recyclerView.setBackgroundColor(Color.parseColor("#ffffff"));
                    initRv();
                    progressDialog.dismiss();

                } else {
                    license.setVisibility(View.VISIBLE);
                    registration.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    checkUserLicense();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }

    private void initRv() {
        FirebaseDatabase.getInstance().getReference().child("usersData")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            assert userModel != null;
                            if (userModel.getType().equals("individual")) {
                                userModel.setUid(dataSnapshot.getKey());
                                userModelList.add(userModel);
                            }
                        }
                        Log.d("List Size", "checkUserType: " + userModelList.size());
                        userDataAdapter = new UserDataAdapter(userModelList, MainActivity.this);
                        recyclerView.setAdapter(userDataAdapter);
                        userDataAdapter.notifyDataSetChanged();
                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                userDataAdapter.getFilter().filter(newText);
                                return true;
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}