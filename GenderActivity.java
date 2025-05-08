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

public class GenderActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private Button nextQuestionButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        genderRadioGroup = findViewById(R.id.gender_radio_group);
        nextQuestionButton = findViewById(R.id.btn_next_question);

        int beigeColor = ContextCompat.getColor(this, R.color.beige);
        ColorStateList beigeTint = ColorStateList.valueOf(beigeColor);
        ((RadioButton) findViewById(R.id.radio_female)).setButtonTintList(beigeTint);
        ((RadioButton) findViewById(R.id.radio_male)).setButtonTintList(beigeTint);
        ((RadioButton) findViewById(R.id.radio_not_say)).setButtonTintList(beigeTint);

        nextQuestionButton.setOnClickListener(v -> {
            int selectedId = genderRadioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                String gender = selectedRadioButton.getText().toString();

                String userId = mAuth.getCurrentUser().getUid();
                databaseRef.child(userId).child("gender").setValue(gender)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Gender saved!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AnalyseRoutineActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to save gender", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
