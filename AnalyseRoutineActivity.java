package com.example.fyp_clearcanvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
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

    //firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_routine);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        //initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //initialize the RadioGroup and Button
        routineRadioGroup = findViewById(R.id.skincare_routine_radio_group);
        nextButton = findViewById(R.id.btn_next_question);

        RadioButton radio_yes = findViewById(R.id.radio_yes);
        RadioButton radio_no = findViewById(R.id.radio_no);

        radio_yes.setButtonTintList(ContextCompat.getColorStateList(this, R.color.beige));
        radio_no.setButtonTintList(ContextCompat.getColorStateList(this, R.color.beige));


        //Next button's onClick listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the selected RadioButton ID
                int selectedId = routineRadioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    //Check whats selected
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedOption = selectedRadioButton.getText().toString();

                    //save the user's response to Firebase
                    saveRoutineToFirebase(selectedOption);

                    if (selectedOption.equals("Yes")) {
                        Intent intent = new Intent(AnalyseRoutineActivity.this, AnalyseMultipleActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AnalyseRoutineActivity.this, "Let's Fix That!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(AnalyseRoutineActivity.this, AnalyseTypeActivity.class);
                            startActivity(intent);
                            finish();
                        }, 1000);
                    }

                } else {
                    //show a Toast message if no option is selected
                    Toast.makeText(AnalyseRoutineActivity.this, "Please Select an Option", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return;
    }

    private void saveRoutineToFirebase(String routineAnswer) {
        //get the current user's UID
        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            //save the selected skincare routine response to Firebase
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
