package com.example.fyp_clearcanvas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private ImageView profileIcon, settingsIcon, analyseSkinButton, skinStateButton, consultProfessionalButton,
            recommendedProductsButton, viewProductHistoryButton, shopProductsButton;
    private Button quickViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize the UI elements
        // profileIcon = findViewById(R.id.profileIcon);
        // settingsIcon = findViewById(R.id.settingsIcon);
        analyseSkinButton = findViewById(R.id.analyseSkinButton);
        //skinStateButton = findViewById(R.id.skinStateButton);
        // consultProfessionalButton = findViewById(R.id.consultProfessionalButton);
        recommendedProductsButton = findViewById(R.id.recommendedProductsButton);
        // viewProductHistoryButton = findViewById(R.id.viewProductHistoryButton);
        //shopProductsButton = findViewById(R.id.shopProductsButton);

        // Set click listeners for each button
        // profileIcon.setOnClickListener(v -> {
        // Navigate to Profile Activity (replace ProfileActivity.class with actual profile activity class)
        //    startActivity(new Intent(Menu.this, ProfileActivity.class));


        // settingsIcon.setOnClickListener(v -> {
        // Navigate to Settings Activity (replace SettingsActivity.class with actual settings activity class)
        //   startActivity(new Intent(Menu.this, SettingsActivity.class));
        //  });

        analyseSkinButton.setOnClickListener(v -> {
            // Navigate to Analyse Skin Activity
            startActivity(new Intent(MenuActivity.this, AnalyseTypeActivity.class));
        });

        //skinStateButton.setOnClickListener(v -> {
        // Navigate to Skin State Activity
        //    startActivity(new Intent(Menu.this, SkinState.class));
        // });

        //consultProfessionalButton.setOnClickListener(v -> {
        // Navigate to Consult Professional Activity
        //  startActivity(new Intent(Menu.this, ConsultProfessional.class));
        //  });

        recommendedProductsButton.setOnClickListener(v -> {
            // Navigate to Recommended Products Activity
            startActivity(new Intent(MenuActivity.this, RecommendActivity.class));
        });

        // viewProductHistoryButton.setOnClickListener(v -> {
        // Navigate to View Product History Activity
        // startActivity(new Intent(Menu.this, ViewProductHistory.class));
        //  });

        //shopProductsButton.setOnClickListener(v -> {
        // Navigate to Shop Products Activity
        // startActivity(new Intent(Menu.this, RecommendedProducts.class));
        //});
        return;
    }
}