package com.example.fyp_clearcanvas;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ViewHolder> {

    private final Context context;
    private final List<Consultation> consultationList;
    private final OnConsultationClickListener clickListener;


    public ConsultationAdapter(Context context, List<Consultation> consultationList, OnConsultationClickListener clickListener) {
        this.context = context;
        this.consultationList = consultationList;
        this.clickListener = clickListener;
    }


    public ConsultationAdapter(Context context, List<Consultation> consultationList) {
        this(context, consultationList, null);
    }

    public interface OnConsultationClickListener {
        void onConsultationClick(Consultation consultation);
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

        holder.dateText.setText("Date: " + consultation.getDate());
        holder.skinTypeText.setText("Skin Type: " + consultation.getSkinType());

        if (consultation.getImageUrl() != null && !consultation.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(consultation.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onConsultationClick(consultation);
            } else {

                Intent intent = new Intent(context, ConsultationViewActivity.class);
                intent.putExtra("consultationId", consultation.getConsultationId());
                intent.putExtra("imageUrl", consultation.getImageUrl());
                intent.putExtra("date", consultation.getDate());
                intent.putExtra("skinType", consultation.getSkinType());
                intent.putExtra("result", consultation.getResult());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consultationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView dateText, skinTypeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.consultationImage);
            dateText = itemView.findViewById(R.id.dateTextView);
            skinTypeText = itemView.findViewById(R.id.skinTypeTextView);
        }
    }
}
