package com.example.fyp_clearcanvas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_clearcanvas.Consultation;
import com.example.fyp_clearcanvas.ConsultationAdapter;
import com.example.fyp_clearcanvas.MenuActivity;
import com.example.fyp_clearcanvas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviousActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConsultationAdapter adapter;
    private List<Consultation> consultationList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Button btnBack, btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_item);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        consultationList = new ArrayList<>();
        adapter = new ConsultationAdapter(this, consultationList);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        fetchPreviousConsultations();

        // Find Buttons
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);

        // Back button to go to the previous activity
        btnBack.setOnClickListener(v -> finish());

        // Menu button to navigate to MenuActivity
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(PreviousActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private void fetchPreviousConsultations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("Firebase", "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("consultations");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consultationList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Consultation consultation = dataSnapshot.getValue(Consultation.class);
                    if (consultation != null) {
                        consultationList.add(consultation);
                    }
                }

                Collections.sort(consultationList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviousActivity.this, "Failed to load consultations", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error", error.toException());
            }
        });
    }
}
