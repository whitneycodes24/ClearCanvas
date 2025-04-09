package com.example.fyp_clearcanvas;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private List<WishlistProduct> wishlist;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wishlist = new ArrayList<>();
        adapter = new WishlistAdapter(wishlist, this);
        recyclerView.setAdapter(adapter);

        loadWishlist();
    }

    private void loadWishlist() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("wishlist");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlist.clear();
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    WishlistProduct product = productSnap.getValue(WishlistProduct.class);
                    if (product != null) wishlist.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Wishlist", "Failed to load wishlist: " + error.getMessage());
            }
        });
    }
}
