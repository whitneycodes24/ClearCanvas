package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        private TextView titleTextView;
        private TextView subtitleTextView;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main); // Replace with the correct XML file name if different

            // Initialize views
            titleTextView = findViewById(R.id.title);
            subtitleTextView = findViewById(R.id.subtitle);
            Button getStartedButton = findViewById(R.id.btn_get_started);
            Button loginButton = findViewById(R.id.btn_login);

            // Set button click listeners
            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to SignUp activity
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to Login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }
    }
