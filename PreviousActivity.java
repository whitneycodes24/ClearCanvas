package com.example.fyp_clearcanvas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
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
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button btnMenu, btnBack;
    private ProgressBar progressBar;
    private TextView noConsultationsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnMenu = findViewById(R.id.btnMenu);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        noConsultationsText = findViewById(R.id.noConsultationsText); // Ensure this exists in XML

        consultationList = new ArrayList<>();
        adapter = new ConsultationAdapter(this, consultationList);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("consultations");

        fetchPreviousConsultations();

        // Navigation Buttons
        btnMenu.setOnClickListener(v -> startActivity(new Intent(PreviousActivity.this, MenuActivity.class)));
        btnBack.setOnClickListener(v -> finish());

        // Enable Swipe to Delete with Undo
        enableSwipeToDelete();
    }

    private void fetchPreviousConsultations() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noConsultationsText.setVisibility(View.GONE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("Firebase", "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("consultations");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consultationList.clear(); // Clear previous data

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    noConsultationsText.setVisibility(View.VISIBLE);
                    noConsultationsText.setText("No previous consultations available.");
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Consultation consultation = dataSnapshot.getValue(Consultation.class);
                    if (consultation != null) {
                        consultationList.add(consultation);
                    }
                }

                // Sort by timestamp (latest first)
                Collections.sort(consultationList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviousActivity.this, "Failed to load consultations", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Database error", error.toException());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Consultation consultation = consultationList.get(position);

                // Remove from Firebase
                userRef.child(consultation.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            consultationList.remove(position);
                            adapter.notifyItemRemoved(position);
                            showUndoSnackbar(consultation, position);
                        })
                        .addOnFailureListener(e -> {
                            adapter.notifyItemChanged(position);
                            Toast.makeText(PreviousActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        });
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    private void showUndoSnackbar(Consultation consultation, int position) {
        Snackbar snackbar = Snackbar.make(recyclerView, "Consultation deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> {
            // Re-add the consultation
            userRef.child(consultation.getId()).setValue(consultation)
                    .addOnSuccessListener(aVoid -> {
                        consultationList.add(position, consultation);
                        adapter.notifyItemInserted(position);
                    });
        });
        snackbar.show();
    }
}
