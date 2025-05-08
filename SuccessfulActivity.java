package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        TextView successMessage = findViewById(R.id.successMessage);
        Button goToMenuButton = findViewById(R.id.menuButton);
        Button consultationButton = findViewById(R.id.consultationButton);


        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("name")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.exists() ? snapshot.getValue(String.class) : "there";
                    String message = "Welcome, " + name + "!\nYou're now a Premium Member!\nYou're one step closer to your Clear Canvas journey.";
                    successMessage.setText(message);
                })
                .addOnFailureListener(e -> {
                    successMessage.setText("You're now a Premium Member!\nYou're one step closer to your Clear Canvas journey.");
                });

        consultationButton.setOnClickListener(v -> {
            startActivity(new Intent(SuccessfulActivity.this, AnalyseCameraActivity.class));
            finish();
        });

        goToMenuButton.setOnClickListener(v -> {
            startActivity(new Intent(SuccessfulActivity.this, MenuActivity.class));
            finish();
        });
    }
}
