package com.example.fyp_clearcanvas;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final List<WishlistProduct> wishlistItems;
    private final Context context;

    public WishlistAdapter(List<WishlistProduct> wishlistItems, Context context) {
        this.wishlistItems = wishlistItems;
        this.context = context;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistProduct product = wishlistItems.get(position);
        holder.productName.setText(product.getName());

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));
            context.startActivity(intent);
        });

        holder.btnRemove.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance()
                    .getReference("Wishlist")
                    .child(userId)
                    .child(product.getProductId())
                    .removeValue();

            wishlistItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wishlistItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        Button btnView, btnRemove;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.wishlistProductName);
            btnView = itemView.findViewById(R.id.btnViewProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
