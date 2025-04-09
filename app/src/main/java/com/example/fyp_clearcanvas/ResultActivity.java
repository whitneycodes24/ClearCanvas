package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private TextView skinTypeTV, resultTV;
    private Button btnSaveConsultation;
    private ImageView consultationImage;

    private String skinType, skinResult, acneType, imageUrl;
    private FirebaseAuth mAuth;

    private static final String API_URL = "http://192.168.0.6:8080/api/v1/product";  //your local API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        recyclerView = findViewById(R.id.recyclerView);
        skinTypeTV = findViewById(R.id.skinTypeTV);
        resultTV = findViewById(R.id.resultTV);
        btnSaveConsultation = findViewById(R.id.btnSaveConsultation);
        Button btnReturnToMainMenu = findViewById(R.id.btnReturnToMainMenu);
        consultationImage = findViewById(R.id.consultationImage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        skinResult = getIntent().getStringExtra("result");
        skinType = getIntent().getStringExtra("skinType");
        imageUrl = getIntent().getStringExtra("imageUrl");
        acneType = getIntent().getStringExtra("acneType");

        Log.d("ResultActivity", "Received skinType: " + skinType);
        Log.d("ResultActivity", "Received result: " + skinResult);
        Log.d("ResultActivity", "Received imageUrl: " + imageUrl);
        Log.d("ResultActivity", "Received acneType: " + acneType);

        // Load the uploaded GCS image into the ImageView
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(consultationImage);
        } else {
            Log.e("ResultActivity", "No image URL provided");
        }

        resultTV.setText(skinResult != null && !skinResult.isEmpty() ?
                "Your Personalised Skin Result: " + skinResult : "Skin Result Not Found");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        fetchSkinTypeFromFirebase();

        btnSaveConsultation.setOnClickListener(v -> saveResultsToFirebase());

        btnReturnToMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchSkinTypeFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("Firebase", "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = databaseReference.child(userId).child("skinType");

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                skinType = task.getResult().getValue(String.class);
                skinTypeTV.setText("Your Skin Type: " + skinType);
                Log.d("Firebase", "Fetched skinType from Firebase: " + skinType);
                if (skinType != null && !skinType.isEmpty()) {
                    fetchProductsForSkinType(skinType);
                }
            } else {
                Log.e("Firebase", "Failed to fetch skinType", task.getException());
            }
        });
    }

    private void fetchProductsForSkinType(String skinType) {
        if (skinType == null || skinType.isEmpty()) {
            Log.e("API_ERROR", "Skin type is null or empty");
            return;
        }

        String formattedSkinType = formatSkinType(skinType);
        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("skinType", formattedSkinType);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                    response -> {
                        try {
                            productList.clear();
                            JSONArray jsonArray = response.getJSONArray("products");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productObject = jsonArray.getJSONObject(i);
                                String name = productObject.getString("name");
                                String priceString = productObject.getString("price").replaceAll("[^\\d.]", "");
                                double price = priceString.isEmpty() ? 0.0 : Double.parseDouble(priceString);
                                String link = productObject.getString("link");

                                productList.add(new Product(name, price, link));
                            }

                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing product data", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("API_ERROR", "Failed to fetch products: " + error.getMessage(), error);
                        Toast.makeText(this, "Failed to get products", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatSkinType(String skinType) {
        if (skinType == null) return "unknown";

        switch (skinType.toLowerCase()) {
            case "combination skin": return "combination skin";
            case "normal skin": return "normal skin";
            case "dry skin": return "dry skin";
            case "oily skin": return "oily skin";
            case "acne skin": return "acne skin";
            default: return "unknown";
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

        userRef.child("consultations").get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                userRef.child("consultations").setValue("");
            }

            DatabaseReference consultationsRef = userRef.child("consultations");
            String consultationId = consultationsRef.push().getKey();

            if (consultationId == null) return;

            long timestamp = System.currentTimeMillis();
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));

            Consultation consultation = new Consultation(
                    consultationId,
                    imageUrl,
                    skinResult,
                    acneType,
                    skinType,
                    date,
                    timestamp
            );

            consultationsRef.child(consultationId).setValue(consultation).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Toast.makeText(ResultActivity.this, "Consultation Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultActivity.this, "Failed to Save Consultation!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
