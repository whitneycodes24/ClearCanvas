package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private Button saveChangesButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User Not Logged In", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        saveChangesButton.setOnClickListener(v -> updateProfile());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MenuActivity.class));
            finish();
        });

    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter At Least One Field To Update.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean[] updated = {false};

        if (!TextUtils.isEmpty(name)) {
            userRef.child("name").setValue(name);
            updated[0] = true;
        }

        if (!TextUtils.isEmpty(email)) {
            if (!email.contains("@") || !email.endsWith(".com") || email.length() < 6) {
                Toast.makeText(this, "Invalid Email - Must Be At Least 6 Characters, Contain '@', And End With '.com'", Toast.LENGTH_SHORT).show();
            } else {
                currentUser.updateEmail(email)
                        .addOnSuccessListener(unused -> {
                            userRef.child("email").setValue(email);
                            updated[0] = true;
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Email Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }

        if (!TextUtils.isEmpty(password)) {
            if (password.length() < 6) {
                Toast.makeText(this, "Password Must Be At Least 6 Characters.", Toast.LENGTH_SHORT).show();
            } else {
                currentUser.updatePassword(password)
                        .addOnSuccessListener(unused -> updated[0] = true)
                        .addOnFailureListener(e -> Toast.makeText(this, "Password Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }


        saveChangesButton.postDelayed(() -> {
            if (updated[0]) {
                showSuccessDialog();
            }
        }, 800);
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Profile Updated")
                .setMessage("Your profile Has Been Successfully Updated!\nYou're One Step Closer To Your Clear Canvas.")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(ProfileActivity.this, MenuActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
