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

    //declare checkboxes and button
    private CheckBox faceWashCheckBox, facialMoisturiserCheckBox, exfoliatorCheckBox, tonersCheckBox, serumsCheckBox, spfCheckBox, eyeCreamCheckBox;
    private Button submitButton;

    //firebase authentication and database reference
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_multiple);

        //initialize Firebase authentication and database reference
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        //initialize checkboxes
        faceWashCheckBox = findViewById(R.id.checkbox_face_wash);
        facialMoisturiserCheckBox = findViewById(R.id.checkbox_facial_moisturiser);
        exfoliatorCheckBox = findViewById(R.id.checkbox_exfoliator);
        tonersCheckBox = findViewById(R.id.checkbox_toners);
        serumsCheckBox = findViewById(R.id.checkbox_serums);
        spfCheckBox = findViewById(R.id.checkbox_spf);
        eyeCreamCheckBox = findViewById(R.id.checkbox_eyecream);

        //initialize the submit button
        submitButton = findViewById(R.id.btn_submitroutine);

        //onClickListener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the list of selected skincare routine items
                ArrayList<String> selectedItems = getSelectedItems();

                if (!selectedItems.isEmpty()) {
                    //save the selected routine to Firebase and navigate to CameraActivity
                    saveToFirebase(selectedItems);
                } else {
                    //display a Toast if no item is selected
                    Toast.makeText(AnalyseMultipleActivity.this, "Please select at least one item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //method to get the list of selected skincare routine items
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

    //method to save the selected skincare routine to Firebase
    private void saveToFirebase(ArrayList<String> selectedItems) {
        // Get the curren user's unique ID
        String userId = auth.getCurrentUser().getUid();

        //prepare the data to be updated in the database
        Map<String, Object> updates = new HashMap<>();
        updates.put("oldRoutine", selectedItems); // Save skincare routine under "oldRoutine"

        //update the database
        databaseRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AnalyseMultipleActivity.this, "Routine saved successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AnalyseMultipleActivity.this, AnalyseTypeActivity.class);
                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AnalyseMultipleActivity.this, "Failed to save routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
