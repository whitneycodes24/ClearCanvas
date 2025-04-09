package com.example.fyp_clearcanvas;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ViewHolder> {

    private final Context context;
    private final List<Consultation> consultationList;

    public ConsultationAdapter(Context context, List<Consultation> consultationList) {
        this.context = context;
        this.consultationList = consultationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.consultation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Consultation consultation = consultationList.get(position);

        holder.skinTypeTextView.setText("Skin Type: " + consultation.getSkinType());
        holder.dateTextView.setText("Date: " + consultation.getDate());

        if (consultation.getImageUrl() != null && !consultation.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(consultation.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.consultationImage);
        } else {
            holder.consultationImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConsultationViewActivity.class);
            intent.putExtra("result", consultation.getResult());
            intent.putExtra("acneType", consultation.getAcneType());
            intent.putExtra("skinType", consultation.getSkinType());
            intent.putExtra("date", consultation.getDate());
            intent.putExtra("imageUrl", consultation.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consultationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView consultationImage;
        TextView skinTypeTextView, dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consultationImage = itemView.findViewById(R.id.consultationImage);
            skinTypeTextView = itemView.findViewById(R.id.skinTypeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
