package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_type);

        // initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        skinTypeRadioGroup = findViewById(R.id.skin_type_radio_group);
        nextQuestionButton = findViewById(R.id.btn_next_question);

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = skinTypeRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedSkinType = selectedRadioButton.getText().toString();

                    Toast.makeText(AnalyseTypeActivity.this, "Selected Skin Type: " + selectedSkinType, Toast.LENGTH_SHORT).show();

                    saveSkinTypeToFirebase(selectedSkinType);

                    Intent intent = new Intent(AnalyseTypeActivity.this, AnalyseCameraActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AnalyseTypeActivity.this, "Please select a skin type", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return;
    }

    private void saveSkinTypeToFirebase(String skinType) {
        //get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("skinType").setValue(skinType).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Skin Type Saved Successfully: " + skinType);
            } else {
                Log.e("Firebase", "Failed to save skinType", task.getException());
            }
        });

    }
}