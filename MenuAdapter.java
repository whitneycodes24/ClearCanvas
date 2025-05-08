package com.example.fyp_clearcanvas;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuOptions> menuOptions;
    private OnMenuItemClickListener listener;

    public MenuAdapter(List<MenuOptions> menuOptions, OnMenuItemClickListener listener) {
        this.menuOptions = menuOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuOptions option = menuOptions.get(position);
        holder.menuPicture.setImageResource(option.getMenuPicture());
        holder.label.setText(option.getLabel());

        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .alpha(0.7f)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        listener.onMenuItemClick(position);
                    })
                    .start();
        });
    }

    @Override
    public int getItemCount() {
        return menuOptions.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView menuPicture;
        TextView label;

        public MenuViewHolder(View itemView) {
            super(itemView);
            menuPicture = itemView.findViewById(R.id.menuPicture);
            label = itemView.findViewById(R.id.menuLabel);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position);
    }
}
