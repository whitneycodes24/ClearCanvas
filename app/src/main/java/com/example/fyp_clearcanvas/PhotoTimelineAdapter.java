package com.example.fyp_clearcanvas;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoTimelineAdapter extends RecyclerView.Adapter<PhotoTimelineAdapter.PhotoViewHolder> {

    private final List<PhotoItem> photoList;
    private final Context context;

    public PhotoTimelineAdapter(List<PhotoItem> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoItem item = photoList.get(position);

        //load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.photoImageView);

        holder.noteTextView.setText(item.getNote());

        //set date
        String formattedDate = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(new Date(item.getTimestamp()));
        holder.dateTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView noteTextView;
        TextView dateTextView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.timelineImage);
            noteTextView = itemView.findViewById(R.id.timelineNote);
            dateTextView = itemView.findViewById(R.id.timelineDate);
        }
    }
}
