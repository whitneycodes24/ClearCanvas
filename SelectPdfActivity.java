package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SelectPdfActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Consultation> consultationArrayList = new ArrayList<>();
    private ConsultationAdapter adapter;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pdf);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        recyclerView = findViewById(R.id.recyclerViewConsultations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConsultationAdapter(this, consultationArrayList, selectedConsultation -> {
            Intent intent = new Intent(this, PdfActivity.class);
            intent.putExtra("consultation", selectedConsultation);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Newest First", "Skin Type")
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                sortConsultations(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("consultations");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consultationArrayList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Consultation consultation = child.getValue(Consultation.class);
                    if (consultation != null) consultationArrayList.add(consultation);
                }
                sortConsultations("Newest First");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectPdfActivity.this, "Failed to load consultations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortConsultations(String option) {
        switch (option) {
            case "Newest First":
                Collections.sort(consultationArrayList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case "Skin Type":
                Collections.sort(consultationArrayList, Comparator.comparing(Consultation::getSkinType));
                break;
        }
        adapter.notifyDataSetChanged();
    }
}
