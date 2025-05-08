package com.example.fyp_clearcanvas;


import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsultationViewActivity extends AppCompatActivity {

    private ImageView consultationImage;
    private TextView dateTextView, skinTypeTextView, resultTextView, noNotesText;
    private Button btnBack, btnAddNote;
    private RecyclerView notesRecyclerView;

    private SkinNoteAdapter noteAdapter;
    private List<SkinNote> noteList = new ArrayList<>();
    private DatabaseReference diaryRef;
    private String consultationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_previous);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        consultationImage = findViewById(R.id.consultationImage);
        dateTextView = findViewById(R.id.dateTextView);
        skinTypeTextView = findViewById(R.id.skinTypeTextView);
        resultTextView = findViewById(R.id.resultTextView);
        noNotesText = findViewById(R.id.noNotesText);
        btnBack = findViewById(R.id.btnBack);
        btnAddNote = findViewById(R.id.btnAddNote);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);


        noteAdapter = new SkinNoteAdapter(this, noteList, consultationId);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(noteAdapter);


        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("date");
        String skinType = getIntent().getStringExtra("skinType");
        String result = getIntent().getStringExtra("result");
        consultationId = getIntent().getStringExtra("consultationId");


        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder_image).into(consultationImage);
        } else {
            consultationImage.setImageResource(R.drawable.placeholder_image);
        }

        dateTextView.setText("Date: " + (date != null ? date : "Unknown"));
        skinTypeTextView.setText("Skin Type: " + (skinType != null ? skinType : "Unknown"));
        resultTextView.setText("AI Analysis: " + (result != null ? result : "No result"));

        btnBack.setOnClickListener(v -> finish());


        if (consultationId != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            diaryRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("consultations")
                    .child(consultationId)
                    .child("diary");

            fetchDiaryNotes();
        } else {
            Toast.makeText(this, "Missing Consultation ID", Toast.LENGTH_SHORT).show();
        }

        btnAddNote.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_note, null);
        builder.setView(dialogView);

        EditText noteEditText = dialogView.findViewById(R.id.noteEditText);
        Button saveNoteButton = dialogView.findViewById(R.id.saveNoteButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        saveNoteButton.setOnClickListener(v -> {
            String noteText = noteEditText.getText().toString().trim();
            if (!noteText.isEmpty()) {
                String noteId = diaryRef.push().getKey();
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                SkinNote newNote = new SkinNote(noteId, noteText, timestamp);

                diaryRef.child(noteId).setValue(newNote)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            showNoteSavedPopup();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed To Save Note", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Please Enter A Note.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoteSavedPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.successful_note, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(popupView);

        AlertDialog popupDialog = builder.create();
        popupDialog.show();

        new android.os.Handler().postDelayed(popupDialog::dismiss, 2000); //dismissed
    }

    private void fetchDiaryNotes() {
        diaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                noteList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        SkinNote note = child.getValue(SkinNote.class);
                        if (note != null) {
                            noteList.add(note);
                        }
                    }
                    noteAdapter.notifyDataSetChanged();
                    noNotesText.setVisibility(noteList.isEmpty() ? TextView.VISIBLE : TextView.GONE);
                } else {
                    noNotesText.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ConsultationViewActivity.this, "Failed To Load Notes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
