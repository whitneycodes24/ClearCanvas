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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.example.fyp_clearcanvas.WishlistProduct;
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
        holder.productPrice.setText(String.format("€%.2f", product.getPrice()));

        // View Product button
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));
            context.startActivity(intent);
        });

        // Remove button
        holder.btnRemove.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(user.getUid())
                        .child("wishlist")
                        .child(product.getName())
                        .removeValue();

                wishlistItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, wishlistItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        Button btnView, btnRemove;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.wishlistProductName);
            productPrice = itemView.findViewById(R.id.wishlistProductPrice);
            btnView = itemView.findViewById(R.id.btnViewProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
