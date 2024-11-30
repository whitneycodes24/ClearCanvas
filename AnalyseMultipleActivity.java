package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyseMultipleActivity extends AppCompatActivity {

    private CheckBox faceWashCheckBox, facialMoisturiserCheckBox, exfoliatorCheckBox, tonersCheckBox, serumsCheckBox, spfCheckBox, eyeCreamCheckBox;
    private Button submitButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_multiple);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize checkboxes
        faceWashCheckBox = findViewById(R.id.checkbox_face_wash);
        facialMoisturiserCheckBox = findViewById(R.id.checkbox_facial_moisturiser);
        exfoliatorCheckBox = findViewById(R.id.checkbox_exfoliator);
        tonersCheckBox = findViewById(R.id.checkbox_toners);
        serumsCheckBox = findViewById(R.id.checkbox_serums);
        spfCheckBox = findViewById(R.id.checkbox_spf);
        eyeCreamCheckBox = findViewById(R.id.checkbox_eyecream);

        // Initialize the submit button
        submitButton = findViewById(R.id.btn_submitroutine);

        // Set up the submit button's onClick listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedItems = getSelectedItems();

                if (!selectedItems.isEmpty()) {
                    saveToFirebase(selectedItems); // Save selected items to Firebase
                } else {
                    Toast.makeText(AnalyseMultipleActivity.this, "Please select at least one item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to check which items are selected
    private ArrayList<String> getSelectedItems() {
        ArrayList<String> selectedItems = new ArrayList<>();

        if (faceWashCheckBox.isChecked()) selectedItems.add("Face Wash");
        if (facialMoisturiserCheckBox.isChecked()) selectedItems.add("Facial Moisturiser");
        if (exfoliatorCheckBox.isChecked()) selectedItems.add("Exfoliator");
        if (tonersCheckBox.isChecked()) selectedItems.add("Toners");
        if (serumsCheckBox.isChecked()) selectedItems.add("Serums");
        if (spfCheckBox.isChecked()) selectedItems.add("Sun Protection Factor (SPF)");
        if (eyeCreamCheckBox.isChecked()) selectedItems.add("Eye Cream");

        return selectedItems;
    }

    // Save selected items to Firebase
    private void saveToFirebase(ArrayList<String> selectedItems) {
        String userId = auth.getCurrentUser().getUid();  // Unique user ID
        Map<String, Object> updates = new HashMap<>();
        updates.put("oldRoutine", selectedItems); // Save skincare routine under 'oldRoutine'

        databaseRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AnalyseMultipleActivity.this, "Routine saved successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AnalyseMultipleActivity.this, CameraActivity.class);
                    startActivity(intent);
                    finish(); // Finish current activity to prevent returning to it
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AnalyseMultipleActivity.this, "Failed to save routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
