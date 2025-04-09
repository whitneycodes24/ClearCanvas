package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private ImageView analyseSkinButton, pconsultationsButton, progressButton, wishlistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        analyseSkinButton = findViewById(R.id.analyseSkinButton);
        pconsultationsButton = findViewById(R.id.pconsultationsButton);
        progressButton = findViewById(R.id.progressButton);
        wishlistButton = findViewById(R.id.wishlistButton);


        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_fade);


        analyseSkinButton.setOnClickListener(v -> {
                v.startAnimation(buttonAnimation);
                startActivity(new Intent(MenuActivity.this, GenderActivity.class));
    });

        pconsultationsButton.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            startActivity(new Intent(MenuActivity.this, PreviousActivity.class));
        });

        wishlistButton.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            startActivity(new Intent(MenuActivity.this, WishlistActivity.class));
        });

        progressButton.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) return;

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid())
                    .child("consultations");

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    ArrayList<Consultation> consultationList = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Consultation consultation = snapshot.getValue(Consultation.class);
                        if (consultation != null) {
                            consultationList.add(consultation);
                        }
                    }

                    Intent intent = new Intent(MenuActivity.this, GraphActivity.class);
                    intent.putExtra("consultations", consultationList);
                    startActivity(intent);
                }
            });
        });

    }
}
