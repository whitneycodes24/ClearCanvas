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
    private String membershipType;
    private int consultationsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        btnFemale = findViewById(R.id.radio_female);
        btnMale = findViewById(R.id.radio_male);
        btnRatherNotSay = findViewById(R.id.radio_not_say);

        //stop radio buttons at start until membership is checked
        btnFemale.setEnabled(false);
        btnMale.setEnabled(false);
        btnRatherNotSay.setEnabled(false);

        checkMembershipStatus();
    }

    private void checkMembershipStatus() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                membershipType = task.getResult().child("membershipType").getValue(String.class);
                Long consultations = task.getResult().child("consultationsRemaining").getValue(Long.class);
                consultationsRemaining = consultations != null ? consultations.intValue() : 0;

                if ("free".equals(membershipType) && consultationsRemaining <= 0) {
                    Toast.makeText(GenderActivity.this, "You have no free consultations left. Please upgrade!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(GenderActivity.this, PremiumActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    enableGenderSelection();
                }
            } else {
                Toast.makeText(GenderActivity.this, "Failed to fetch membership info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableGenderSelection() {
        btnFemale.setEnabled(true);
        btnMale.setEnabled(true);
        btnRatherNotSay.setEnabled(true);

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
        Toast.makeText(this, "Selected: " + gender, Toast.LENGTH_SHORT).show();

        String userId = mAuth.getCurrentUser().getUid();
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

        Intent intent = new Intent(GenderActivity.this, AnalyseRoutineActivity.class);
        startActivity(intent);
        finish();
    }
}
