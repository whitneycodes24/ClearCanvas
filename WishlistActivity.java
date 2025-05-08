package com.example.fyp_clearcanvas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private List<WishlistProduct> wishlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WishlistAdapter(wishlist, this);
        recyclerView.setAdapter(adapter);

        Button backToMenuButton = findViewById(R.id.backToMenuButton);
        backToMenuButton.setOnClickListener(v -> {
            startActivity(new Intent(WishlistActivity.this, MenuActivity.class));
            finish();
        });

        loadWishlist();
    }

    private void loadWishlist() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId);

        wishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlist.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Object raw = childSnapshot.getValue();
                        if (raw instanceof Map) {
                            try {
                                WishlistProduct product = childSnapshot.getValue(WishlistProduct.class);
                                if (product != null) {
                                    wishlist.add(product);
                                }
                            } catch (Exception e) {
                                Log.e("Wishlist", "Failed to parse product", e);
                            }
                        } else {
                            Log.w("Wishlist", "Skipped non-object wishlist entry: " + raw);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Wishlist", "Failed to load wishlist: " + error.getMessage());
            }
        });
    }
}
