package com.example.fyp_clearcanvas;


import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class SkinNoteAdapter extends RecyclerView.Adapter<SkinNoteAdapter.ViewHolder> {

    private final Context context;
    private final List<SkinNote> noteList;
    private final String consultationId;

    public SkinNoteAdapter(Context context, List<SkinNote> noteList, String consultationId) {
        this.context = context;
        this.noteList = noteList;
        this.consultationId = consultationId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SkinNote note = noteList.get(position);
        holder.noteText.setText(note.getText());
        holder.noteTimestamp.setText(note.getTimestamp());

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Note");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setText(note.getText());
            input.setSelection(note.getText().length());
            input.setPadding(40, 30, 40, 30);

            builder.setView(input);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String updatedText = input.getText().toString().trim();
                if (!updatedText.isEmpty()) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .child("consultations")
                            .child(consultationId)
                            .child("diary")
                            .child(note.getId())
                            .child("text")
                            .setValue(updatedText)
                            .addOnSuccessListener(aVoid -> {
                                note.setText(updatedText);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Note Successfully Updated", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed To Update Note", Toast.LENGTH_SHORT).show());
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("Are You Sure You Want To Delete This Note?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(userId)
                                .child("consultations")
                                .child(consultationId)
                                .child("diary")
                                .child(note.getId())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    noteList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed To Delete Note", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteText, noteTimestamp;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteText = itemView.findViewById(R.id.noteText);
            noteTimestamp = itemView.findViewById(R.id.noteTimestamp);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
