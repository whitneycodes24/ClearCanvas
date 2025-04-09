package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnalyseTypeActivity extends AppCompatActivity {

    private RadioGroup skinTypeRadioGroup;
    private Button nextQuestionButton;

    // Firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_type); // Update with your XML layout name

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        skinTypeRadioGroup = findViewById(R.id.skin_type_radio_group);
        nextQuestionButton = findViewById(R.id.btn_next_question);

        // Set click listener for the Next Question button
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected RadioButton ID
                int selectedId = skinTypeRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    // Find the selected RadioButton and get its text
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedSkinType = selectedRadioButton.getText().toString();

                    // Display a Toast message with the selected skin type
                    Toast.makeText(AnalyseTypeActivity.this, "Selected Skin Type: " + selectedSkinType, Toast.LENGTH_SHORT).show();

                    // Save the selected skin type to Firebase Realtime Database
                    saveSkinTypeToFirebase(selectedSkinType);

                    // Proceed to the next activity (replace NextActivity.class with your next screen's class)
                    Intent intent = new Intent(AnalyseTypeActivity.this, AnalyseRoutineActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                } else {
                    // Show a Toast message if no option is selected
                    Toast.makeText(AnalyseTypeActivity.this, "Please select a skin type", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return;
    }

    private void saveSkinTypeToFirebase(String skinType) {
        // Get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            // Save the selected skin type to Firebase Realtime Database
            databaseReference.child(userId).child("skinType").setValue(skinType)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AnalyseTypeActivity.this, "Skin type saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AnalyseTypeActivity.this, "Failed to save skin type: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AnalyseTypeActivity.this, "Failed to save skin type: User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}