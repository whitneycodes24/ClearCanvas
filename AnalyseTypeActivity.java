package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnalyseTypeActivity extends AppCompatActivity {

    private RadioGroup skinTypeRadioGroup;
    private Button nextQuestionButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_type);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        skinTypeRadioGroup = findViewById(R.id.skin_type_radio_group);
        nextQuestionButton = findViewById(R.id.btn_next_question);


        RadioButton normal = findViewById(R.id.radio_normal_skin);
        RadioButton dry = findViewById(R.id.radio_dry_skin);
        RadioButton oily = findViewById(R.id.radio_oily_skin);
        RadioButton combination = findViewById(R.id.radio_combination_skin);
        RadioButton notSure = findViewById(R.id.radio_not_sure);

        int beigeColor = ContextCompat.getColor(this, R.color.beige);
        ColorStateList beigeTint = ColorStateList.valueOf(beigeColor);

        normal.setButtonTintList(beigeTint);
        dry.setButtonTintList(beigeTint);
        oily.setButtonTintList(beigeTint);
        combination.setButtonTintList(beigeTint);
        notSure.setButtonTintList(beigeTint);

        nextQuestionButton.setOnClickListener(v -> {
            int selectedId = skinTypeRadioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                String selectedSkinType = selectedRadioButton.getText().toString();

                Toast.makeText(this, "Selected Skin Type: " + selectedSkinType, Toast.LENGTH_SHORT).show();

                saveSkinTypeToFirebase(selectedSkinType);

                Intent intent = new Intent(this, AnalyseCameraActivity.class);
                intent.putExtra("skinType", selectedSkinType);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please select a skin type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSkinTypeToFirebase(String skinType) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child(userId);

        userRef.child("skinType").setValue(skinType).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Skin Type Saved Successfully: " + skinType);
            } else {
                Log.e("Firebase", "Failed to save skinType", task.getException());
            }
        });
    }
}
