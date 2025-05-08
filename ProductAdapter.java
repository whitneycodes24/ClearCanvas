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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final Set<String> wishlistIds;

    public ProductAdapter(Context context, List<Product> productList, Set<String> wishlistIds) {
        this.context = context;
        this.productList = productList;
        this.wishlistIds = wishlistIds != null ? wishlistIds : new HashSet<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());

        // Set heart icon filled or not
        boolean isWishlisted = wishlistIds.contains(product.getProductId());
        holder.btnWishlist.setText(isWishlisted ? "♥ Wishlist" : "♡ Wishlist");

        holder.btnLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));
            context.startActivity(browserIntent);
        });

        holder.btnWishlist.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference wishlistRef = FirebaseDatabase.getInstance()
                    .getReference("Wishlist")
                    .child(userId)
                    .child(product.getProductId());

            if (wishlistIds.contains(product.getProductId())) {
                wishlistRef.removeValue();
                wishlistIds.remove(product.getProductId());
                holder.btnWishlist.setText("♡ Wishlist");
            } else {
                WishlistProduct wp = new WishlistProduct(product.getProductId(), product.getName(), product.getLink());
                wishlistRef.setValue(wp);
                wishlistIds.add(product.getProductId());
                holder.btnWishlist.setText("♥ Wishlist");
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button btnLink, btnWishlist;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            btnLink = itemView.findViewById(R.id.btnLink);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
        }
    }
}
