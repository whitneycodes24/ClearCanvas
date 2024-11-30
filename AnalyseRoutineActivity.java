package com.example.fyp_clearcanvas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnalyseRoutineActivity extends AppCompatActivity {

    private RadioGroup routineRadioGroup;
    private Button nextButton;

    // Firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_routine); // Link to the XML layout

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize the RadioGroup and Button
        routineRadioGroup = findViewById(R.id.skincare_routine_radio_group);
        nextButton = findViewById(R.id.btn_next_question);

        // Set up Next button's onClick listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected RadioButton ID
                int selectedId = routineRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    // Check if "Yes" or "No" is selected
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedOption = selectedRadioButton.getText().toString();

                    // Save the user's response to Firebase
                    saveRoutineToFirebase(selectedOption);

                    if (selectedOption.equals("Yes")) {
                        // Navigate to the multiple-choice question screen
                        Intent intent = new Intent(AnalyseRoutineActivity.this, AnalyseMultipleActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    } else {
                        // Display a toast message, then navigate to the Menu screen
                        Toast.makeText(AnalyseRoutineActivity.this, "Let's Fix That!", Toast.LENGTH_SHORT).show();

                        // Use a Handler to introduce a delay before navigating
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(AnalyseRoutineActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        }, 2000); // 2000 milliseconds = 2 seconds delay
                    }
                } else {
                    // Show a Toast message if no option is selected
                    Toast.makeText(AnalyseRoutineActivity.this, "Please Select an Option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveRoutineToFirebase(String routineAnswer) {
        // Get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            // Save the selected skincare routine response to Firebase
            databaseReference.child(userId).child("skincareRoutine").setValue(routineAnswer)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AnalyseRoutineActivity.this, "Skincare routine response saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AnalyseRoutineActivity.this, "Failed to save response: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AnalyseRoutineActivity.this, "Failed to save response: User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
