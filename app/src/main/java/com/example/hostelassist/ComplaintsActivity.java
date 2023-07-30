package com.example.hostelassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class ComplaintsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        CommonFunctions.setUser(this);

        findViewById(R.id.include_maintenance).setVisibility(View.VISIBLE);
        ImageButton registerButton = findViewById(R.id.maintenance_register);
        ImageButton statusButton = findViewById(R.id.maintenance_status);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        registerButton.setOnClickListener(view -> startActivity(new Intent(ComplaintsActivity.this, ComplainRegister.class)));

        statusButton.setOnClickListener(view -> startActivity(new Intent(ComplaintsActivity.this, ComplainStatus.class)));

        setUpImageSlider();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return CommonFunctions.navigationItemSelect(item, this);
    }

    public void setUpImageSlider() {
        ViewPager viewPager = findViewById(R.id.ultra_viewpager);
        viewPager.setOverScrollMode(viewPager.getNestedScrollAxes());

        //initialize UltraPagerAdapter，and add child view to UltraViewPager
        PagerAdapter adapter = new SliderAdapter(false,this);
        viewPager.setAdapter(adapter);
    }

}