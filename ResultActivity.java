package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private TextView skinTypeTV, resultTV;
    private Button btnSaveConsultation, addNoteButton;
    private ImageView consultationImage;
    private ProgressBar productLoader;

    private String skinType, skinResult, acneType, imageUrl, consultationId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private static final String API_URL = "http://192.168.1.2:8080/api/v1/product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        recyclerView = findViewById(R.id.recyclerView);
        skinTypeTV = findViewById(R.id.skinTypeTV);
        resultTV = findViewById(R.id.resultTV);
        btnSaveConsultation = findViewById(R.id.btnSaveConsultation);
        addNoteButton = findViewById(R.id.addNoteButton);
        consultationImage = findViewById(R.id.consultationImage);
        Button btnReturnToMainMenu = findViewById(R.id.btnReturnToMainMenu);
        productLoader = findViewById(R.id.productLoader);

        addNoteButton.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        skinResult = getIntent().getStringExtra("result");
        skinType = getIntent().getStringExtra("skinType");
        imageUrl = getIntent().getStringExtra("imageUrl");
        acneType = getIntent().getStringExtra("acneType");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(consultationImage);
        }

        resultTV.setText(skinResult != null && !skinResult.isEmpty()
                ? "Your Personalised Skin Result: " + skinResult
                : "Skin Result Not Found");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId);

        wishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> wishlistIds = new HashSet<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    wishlistIds.add(child.getKey());
                }
                productAdapter = new ProductAdapter(ResultActivity.this, productList, wishlistIds);
                recyclerView.setAdapter(productAdapter);
                fetchSkinTypeFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Set<String> wishlistIds = new HashSet<>();
                productAdapter = new ProductAdapter(ResultActivity.this, productList, wishlistIds);
                recyclerView.setAdapter(productAdapter);
                fetchSkinTypeFromFirebase();
            }
        });

        btnSaveConsultation.setOnClickListener(v -> saveResultsToFirebase());

        addNoteButton.setOnClickListener(v -> {
            if (consultationId != null) {
                Intent intent = new Intent(ResultActivity.this, SkinNoteActivity.class);
                intent.putExtra("consultationId", consultationId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please Save The Consultation First", Toast.LENGTH_SHORT).show();
            }
        });

        btnReturnToMainMenu.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, MenuActivity.class));
            finish();
        });
    }

    private void fetchSkinTypeFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = databaseReference.child(userId).child("skinType");

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                skinType = task.getResult().getValue(String.class);
                skinTypeTV.setText("Your Skin Type: " + skinType);
                fetchProductsForSkinType(skinType);
            }
        });
    }

    private void fetchProductsForSkinType(String skinType) {
        if (skinType == null || skinType.isEmpty()) {
            Log.e("FETCH_PRODUCTS", "Skin type is null or empty");
            return;
        }

        productLoader.setVisibility(View.VISIBLE);
        String formattedSkinType = formatSkinType(skinType);
        Log.d("FETCH_PRODUCTS", "Formatted skin type: " + formattedSkinType);

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JSONObject requestBody = new JSONObject();   //json object for body of request
            requestBody.put("skinType", formattedSkinType);

            Log.d("FETCH_PRODUCTS", "Sending request to " + API_URL + " with body: " + requestBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(   //creating the request
                    Request.Method.POST, API_URL, requestBody,
                    response -> {
                        try {
                            Log.d("FETCH_PRODUCTS", "Response received: " + response.toString());
                            productList.clear();
                            JSONArray jsonArray = response.getJSONArray("products");  //parses array from json
                            Log.d("FETCH_PRODUCTS", "Parsed product array length: " + jsonArray.length());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String name = obj.getString("name");
                                String link = obj.getString("link");
                                Log.d("FETCH_PRODUCTS", "Product parsed: " + name + " (" + link + ")");
                                productList.add(new Product(UUID.randomUUID().toString(), name, link));
                            }

                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("FETCH_PRODUCTS", "JSON parsing error: " + e.getMessage());
                            Toast.makeText(this, "Error parsing product data", Toast.LENGTH_SHORT).show();
                        }
                        productLoader.setVisibility(View.GONE);
                    },
                    error -> {
                        Log.e("FETCH_PRODUCTS", "Volley error: " + error.toString());
                        Toast.makeText(this, "Failed to get products", Toast.LENGTH_SHORT).show();
                        productLoader.setVisibility(View.GONE);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            queue.add(request);
        } catch (JSONException e) {
            Log.e("FETCH_PRODUCTS", "Request body error: " + e.getMessage());
            Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
            productLoader.setVisibility(View.GONE);
        }

}

    @NonNull
    private String formatSkinType(String skinType) {
        if (skinType == null) return "unknown";
        switch (skinType.toLowerCase()) {
            case "combination skin":
            case "normal skin":
            case "dry skin":
            case "oily skin":
            case "acne skin":
                return skinType.toLowerCase();
            default:
                return "unknown";
        }
    }

    private void saveResultsToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        DatabaseReference consultationsRef = userRef.child("consultations");

        consultationId = consultationsRef.push().getKey();
        if (consultationId == null) return;

        long timestamp = System.currentTimeMillis();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));

        int acneRating = extractAcneRating(skinResult);

        Consultation consultation = new Consultation(
                consultationId,
                imageUrl,
                skinResult,
                acneType,
                skinType,
                date,
                timestamp,
                acneRating
        );

        consultationsRef.child(consultationId).setValue(consultation).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Consultation Saved!", Toast.LENGTH_SHORT).show();
                addNoteButton.setEnabled(true);
            } else {
                Toast.makeText(this, "Failed to Save Consultation!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int extractAcneRating(String geminiResult) {
        try {
            geminiResult = geminiResult.replaceAll("[^0-9\\- ]", "").trim();
            if (geminiResult.contains("-")) {
                String[] parts = geminiResult.split("-");
                if (parts.length == 2) {
                    int first = Integer.parseInt(parts[0].trim());
                    int second = Integer.parseInt(parts[1].trim());
                    return Math.round((first + second) / 2f);
                }
            }
            return Integer.parseInt(geminiResult.trim());
        } catch (Exception e) {
            Log.e("AcneRating", "Failed to parse acne rating", e);
            return 0;
        }
    }
}
