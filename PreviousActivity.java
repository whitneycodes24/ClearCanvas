package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviousActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConsultationAdapter adapter;
    private List<Consultation> consultationList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button btnBack;
    private ProgressBar progressBar;
    private TextView noConsultationsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        noConsultationsText = findViewById(R.id.noConsultationsText);

        consultationList = new ArrayList<>();

        adapter = new ConsultationAdapter(this, consultationList, consultation -> {
            Intent intent = new Intent(this, ConsultationViewActivity.class);
            intent.putExtra("consultationId", consultation.getConsultationId());
            intent.putExtra("imageUrl", consultation.getImageUrl());
            intent.putExtra("date", consultation.getDate());
            intent.putExtra("skinType", consultation.getSkinType());
            intent.putExtra("result", consultation.getResult());
            startActivity(intent);
        });

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

        btnBack.setOnClickListener(v -> finish());

        enableSwipeToDelete();
    }

    private void fetchPreviousConsultations() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noConsultationsText.setVisibility(View.GONE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("Firebase", "User Not Logged In");
            Toast.makeText(this, "User Not Logged In", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("consultations");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consultationList.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    noConsultationsText.setVisibility(View.VISIBLE);
                    noConsultationsText.setText("No Previous Consultations Available.");
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Consultation consultation = dataSnapshot.getValue(Consultation.class);
                    if (consultation != null) {
                        consultationList.add(consultation);
                    }
                }

                Collections.sort(consultationList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviousActivity.this, "Failed To Load Consultations", Toast.LENGTH_SHORT).show();
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

                userRef.child(consultation.getConsultationId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            consultationList.remove(position);
                            adapter.notifyItemRemoved(position);
                            showUndoSnackbar(consultation, position);
                        })
                        .addOnFailureListener(e -> {
                            adapter.notifyItemChanged(position);
                            Toast.makeText(PreviousActivity.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                        });
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    private void showUndoSnackbar(Consultation consultation, int position) {
        Snackbar snackbar = Snackbar.make(recyclerView, "Consultation Deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> {
            userRef.child(consultation.getConsultationId()).setValue(consultation)
                    .addOnSuccessListener(aVoid -> {
                        consultationList.add(position, consultation);
                        adapter.notifyItemInserted(position);
                    });
        });
        snackbar.show();
    }
}
