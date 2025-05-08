package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class PaymentActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private static final String BACKEND_URL = "http://192.168.1.2:8080/api/payment/create-payment-intent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_loading);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        PaymentConfiguration.init(this, "pk_test_51RByk4QNr9B64NAMkUzqSl4wcl7rqJidrgTPfLkYRvvixSkhWlB1jfsoEVxaxJCweimx65wmK8e4q3T6pN19EoOx00AgciAkhX");

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        createPaymentIntent(); //calls Stripe payment
    }

    private void createPaymentIntent() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create("{}", mediaType); // send valid JSON instead of empty string

        Log.d("PAYMENT", "Starting payment request to " + BACKEND_URL);

        Request request = new Request.Builder()
                .url(BACKEND_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("PAYMENT", "Network request FAILED", e);
                runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Failed To Start Payment", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("PAYMENT", "Network request completed with status: " + response.code());

                String responseBody = response.body() != null ? response.body().string() : "null";
                Log.d("PAYMENT", "Response body: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        JSONObject result = new JSONObject(responseBody);
                        paymentIntentClientSecret = result.getString("clientSecret");
                        Log.d("PAYMENT", "Received clientSecret: " + paymentIntentClientSecret);
                        runOnUiThread(() -> presentPaymentSheet());
                    } catch (JSONException e) {
                        Log.e("PAYMENT", "JSON parsing error", e);
                        runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Payment Response Parsing Failed", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("PAYMENT", "Server returned error: " + response.code());
                    runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Payment Failed: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void presentPaymentSheet() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("ClearCanvas")
                .build();

        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            upgradeUserToPremium();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            finish();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment Failed - Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }


    private void upgradeUserToPremium() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).child("membershipType").setValue("paid");
        databaseReference.child(userId).child("numConsultations").setValue(-1);

        Toast.makeText(this, "Payment Successful! You Are Now A Premium Member.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(PaymentActivity.this, SuccessfulActivity.class));
        finish();
    }
}
