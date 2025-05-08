package com.example.fyp_clearcanvas;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ConsultationImageAdapter extends RecyclerView.Adapter<ConsultationImageAdapter.ViewHolder> {

    private final List<Consultation> consultationList;
    private final Context context;

    public ConsultationImageAdapter(List<Consultation> consultationList, Context context) {
        this.consultationList = consultationList != null ? consultationList : new ArrayList<>();
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageThumb);
        }
    }

    @NonNull
    @Override
    public ConsultationImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_consultation_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationImageAdapter.ViewHolder holder, int position) {
        Consultation consultation = consultationList.get(position);
        if (consultation.getImageUrl() != null && !consultation.getImageUrl().isEmpty()) {
            Glide.with(context).load(consultation.getImageUrl()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return consultationList.size();
    }
}
