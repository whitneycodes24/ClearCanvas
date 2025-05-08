package com.example.fyp_clearcanvas;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private TextView dateOfBirthTextView, loginLink;
    private Button createAccountButton, backButton;
    private String selectedDateOfBirth;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        dateOfBirthTextView = findViewById(R.id.edit_date_of_birth);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);
        loginLink = findViewById(R.id.loginLink);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        dateOfBirthTextView.setOnClickListener(v -> showDatePicker());
        createAccountButton.setOnClickListener(v -> handleAccountCreation());
        backButton.setOnClickListener(v -> finish());

        String fullText = "Already Have An Account? Log In!";
        SpannableString spannable = new SpannableString(fullText);
        int start = fullText.indexOf("Log In!");
        int end = start + "Log In!".length();
        spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLink.setText(spannable);

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    validateAndSetDate(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void validateAndSetDate(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        if (calendar.after(today)) {
            Toast.makeText(this, "Date of Birth Must Be in the Past", Toast.LENGTH_SHORT).show();
            return;
        }
        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (age < 12 || (age == 12 && today.before(calendar))) {
            Toast.makeText(this, "You Are Too Young For This App", Toast.LENGTH_SHORT).show();
        } else if (age > 100) {
            Toast.makeText(this, "You Are Too Old For This App", Toast.LENGTH_SHORT).show();
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedDateOfBirth = dateFormat.format(calendar.getTime());
            dateOfBirthTextView.setText(selectedDateOfBirth);
        }
    }

    private void handleAccountCreation() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        } else if (!isValidPassword(password)) {
            Toast.makeText(this, "Password Must Have At Least 6 Characters and Contain At Least 1 Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedDateOfBirth)) {
            Toast.makeText(this, "Please Select Your Date of Birth", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                User newUser = new User(userId, name, email, selectedDateOfBirth, "free", 2);
                                databaseReference.child(userId).setValue(newUser)
                                        .addOnCompleteListener(saveTask -> {
                                            if (saveTask.isSuccessful()) {
                                                Intent intent = new Intent(SignUpActivity.this, GenderActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed To Save User Data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isValidEmail(String email) {
        if (email.length() < 6) {
            Toast.makeText(this, "Invalid Email - Must Have At Least 6 Characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email - Must Contain @ and Be a Valid Format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.endsWith(".com")) {
            Toast.makeText(this, "Invalid Email - Must End With .com", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*\\d.*");
    }
}
