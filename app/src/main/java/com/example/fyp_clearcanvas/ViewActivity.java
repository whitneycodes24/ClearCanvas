package com.example.fyp_clearcanvas;


import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoTimelineAdapter adapter;
    private List<PhotoItem> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_previous);

        recyclerView = findViewById(R.id.timelineRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoList = new ArrayList<>();
        adapter = new PhotoTimelineAdapter(photoList, this);
        recyclerView.setAdapter(adapter);

        loadTimelineEntries();
    }

    private void loadTimelineEntries() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("timeline");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    PhotoItem entry = snap.getValue(PhotoItem.class);
                    if (entry != null) {
                        photoList.add(entry);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ViewTimeline", "Failed to load timeline: " + error.getMessage());
            }
        });
    }
}
