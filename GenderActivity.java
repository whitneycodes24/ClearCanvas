package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GenderActivity extends AppCompatActivity {

    private RadioButton btnFemale, btnMale, btnRatherNotSay;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        // Initialize FirebaseAuth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize radio buttons
        btnFemale = findViewById(R.id.radio_female);
        btnMale = findViewById(R.id.radio_male);
        btnRatherNotSay = findViewById(R.id.radio_not_say);

        // Set click listeners for each radio button
        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection("Female");
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection("Male");
            }
        });

        btnRatherNotSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection("Rather Not Say");
            }
        });
    }

    private void handleSelection(String gender) {
        // Show a toast message with the selected option
        Toast.makeText(this, "Selected: " + gender, Toast.LENGTH_SHORT).show();

        // Get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        // Save the selected gender to Firebase Realtime Database
        if (userId != null) {
            databaseReference.child(userId).child("gender").setValue(gender)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Gender saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to save gender: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Navigate to AnalyseTypeActivity
        Intent intent = new Intent(GenderActivity.this, AnalyseTypeActivity.class);
        startActivity(intent);
        finish(); // Close GenderActivity so user cannot navigate back
    }
}
