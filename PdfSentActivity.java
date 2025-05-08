package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PdfSentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_sent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        TextView confirmationMessage = findViewById(R.id.confirmationMessage);
        Button backToMenu = findViewById(R.id.backToMenuButton);

        confirmationMessage.setText("Your Consultation Results Have Been Successfully Emailed to You!");

        ImageView successIcon = findViewById(R.id.successIcon);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fading);
        successIcon.startAnimation(fadeIn);


        backToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(PdfSentActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
