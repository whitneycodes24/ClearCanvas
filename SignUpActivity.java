package com.example.fyp_clearcanvas;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fyp_clearcanvas.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private TextView dateOfBirthTextView;
    private Button createAccountButton, backButton;
    private String selectedDateOfBirth; // To store the valid date of birth

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        dateOfBirthTextView = findViewById(R.id.edit_date_of_birth);
        createAccountButton = findViewById(R.id.btn_create_account);
        backButton = findViewById(R.id.bckbutton);

        // Date picker logic
        dateOfBirthTextView.setOnClickListener(v -> showDatePicker());

        // Set click listener for the create account button
        createAccountButton.setOnClickListener(v -> handleAccountCreation());

        // Set click listener for the back button
        backButton.setOnClickListener(v -> finish()); // Close SignUp activity
        return;
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    validateAndSetDate(calendar);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void validateAndSetDate(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

        // Check if the user is younger than 12 or older than 100
        if (age < 12 || (age == 12 && today.before(calendar))) {
            Toast.makeText(this, "You are too young for this app", Toast.LENGTH_SHORT).show();
        } else if (age > 100) {
            Toast.makeText(this, "You are too old for this app", Toast.LENGTH_SHORT).show();
        } else {
            // Format and display the selected date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedDateOfBirth = dateFormat.format(calendar.getTime());
            dateOfBirthTextView.setText(selectedDateOfBirth);
        }
    }

    private void handleAccountCreation() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic validation for name, email, and password
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters and contain at least 1 number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedDateOfBirth)) {
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
        } else {
            // Create account with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Account created successfully
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save additional user data (like name and DOB) to Firebase Realtime Database
                            if (user != null) {
                                String userId = user.getUid();
                                Toast.makeText(this, "User object created", Toast.LENGTH_LONG).show();
                                // Create a User object to hold the data
                                User newUser = new User(userId, name, email, selectedDateOfBirth);

                                // Save user data under the "Users" node with the unique user ID
                                databaseReference.child(userId).setValue(newUser)
                                        .addOnCompleteListener(saveTask -> {
                                            if (saveTask.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Account created and user info saved", Toast.LENGTH_SHORT).show();

                                                // Proceed to GenderActivity
                                                Intent intent = new Intent(SignUpActivity.this, GenderActivity.class);
                                                startActivity(intent);
                                                finish(); // Close the SignUp activity
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: Could not get user info", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Account creation failed
                            Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Simple email format validation
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Password validation: must be at least 6 characters and contain at least one digit
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*\\d.*");
    }
}
