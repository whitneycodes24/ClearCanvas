package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Collections;
import java.util.List;

public class PremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private Button btn_upgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        btn_upgrade = findViewById(R.id.btn_upgrade);

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

        btn_upgrade.setOnClickListener(v -> launchBillingFlow());
    }

    private void launchBillingFlow() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductId("premium_membership") //get the Google Play Product ID
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()))
                .build();
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getProducts().contains("premium_membership")) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(userId).child("membershipType").setValue("paid");
            databaseReference.child(userId).child("consultationsRemaining").setValue(-1);

            Toast.makeText(this, "Congratulations! You are now a Premium Member!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PremiumActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
