package com.example.fyp_clearcanvas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class RecommendActivity extends AppCompatActivity {

    private ListView productListView;
    private HashMap<String, String> productLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        //productListView = findViewById(R.id.product_list_view);

        // Product Links
        productLinks = new HashMap<>();
        productLinks.put("Facial Wash", "https://www.elizabetharden.com/collections/cleansers");
        productLinks.put("Facial Moisturiser", "https://www.elizabetharden.com/collections/moisturizers");
        // productLinks.put("Exfoliators", "https://www.example.com/exfoliators");
        // productLinks.put("Toners", "https://www.example.com/toners");
        // productLinks.put("Serums", "https://www.example.com/serums");
        productLinks.put("SPF", "https://www.elizabetharden.com/collections/sunscreen");

        // Create a list of product names
        String[] productNames = productLinks.keySet().toArray(new String[0]);

        // Set up the ListView with an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productListView.setAdapter(adapter);

        // Set click listener for the ListView items
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productType = productNames[position];
                String url = productLinks.get(productType);

                // Open the URL in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }
}