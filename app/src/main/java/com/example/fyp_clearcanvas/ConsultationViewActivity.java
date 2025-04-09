package com.example.fyp_clearcanvas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ConsultationViewActivity extends AppCompatActivity {

    private ImageView consultationImage;
    private TextView dateTextView, skinTypeTextView, resultTextView;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_previous);

        consultationImage = findViewById(R.id.consultationImage);
        dateTextView = findViewById(R.id.dateTextView);
        skinTypeTextView = findViewById(R.id.skinTypeTextView);
        resultTextView = findViewById(R.id.resultTextView);
        btnBack = findViewById(R.id.btnBack);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("date");
        String skinType = getIntent().getStringExtra("skinType");
        String result = getIntent().getStringExtra("result");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(consultationImage);
        } else {
            consultationImage.setImageResource(R.drawable.placeholder_image);
        }

        dateTextView.setText("Date: " + (date != null ? date : "Unknown"));
        skinTypeTextView.setText("Skin Type: " + (skinType != null ? skinType : "Unknown"));
        resultTextView.setText("AI Analysis: " + (result != null ? result : "No result"));

        btnBack.setOnClickListener(v -> finish());
    }
}
