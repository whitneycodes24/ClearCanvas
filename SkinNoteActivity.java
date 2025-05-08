package com.example.fyp_clearcanvas;


import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SkinNoteActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Button saveButton;
    private DatabaseReference diaryRef;
    private String userId;
    private String consultationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_note);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveDiaryButton);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        consultationId = getIntent().getStringExtra("consultationId");

        if (consultationId == null || consultationId.isEmpty()) {
            Toast.makeText(this, "Consultation ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        diaryRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("consultations")
                .child(consultationId)
                .child("diary");

        saveButton.setOnClickListener(v -> saveDiaryEntry());
    }

    private void saveDiaryEntry() {
        String entryText = noteEditText.getText().toString().trim();

        if (entryText.isEmpty()) {
            Toast.makeText(this, "Write a Note To Be Saved!", Toast.LENGTH_SHORT).show();
            return;
        }

        String entryId = diaryRef.push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        HashMap<String, String> diaryEntry = new HashMap<>();
        diaryEntry.put("text", entryText);
        diaryEntry.put("timestamp", timestamp);

        diaryRef.child(entryId).setValue(diaryEntry).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Note Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed To Save Note - Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
