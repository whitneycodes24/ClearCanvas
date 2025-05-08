package com.example.fyp_clearcanvas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnalyseCameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final String BASE_URL = "https://detect.roboflow.com/";
    private static final String API_KEY = "MuH4XduWwn4bxx1dwmhz";

    private Button btnPicture;
    private ImageView imageView;
    private Bitmap capturedImage;
    private RoboflowAPI roboflowAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }

        btnPicture = findViewById(R.id.btncamera);
        imageView = findViewById(R.id.imageview1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        roboflowAPI = retrofit.create(RoboflowAPI.class);

        btnPicture.setOnClickListener(v -> checkConsultationsBeforeOpeningCamera());
    }

    private void checkConsultationsBeforeOpeningCamera() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Integer consultationsRemaining = task.getResult().child("numConsultations").getValue(Integer.class);
                if (consultationsRemaining == null) consultationsRemaining = 0;

                if (consultationsRemaining == -1 || consultationsRemaining > 0) {
                    openCameraIfPermitted();
                    if (consultationsRemaining > 0) {
                        databaseReference.child(userId).child("numConsultations").setValue(consultationsRemaining - 1);
                    }
                } else {
                    showUpgradePopup();
                }
            } else {
                Toast.makeText(this, "Failed to check consultations. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCameraIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void showUpgradePopup() {
        new AlertDialog.Builder(this)
                .setTitle("Out of Consultations")
                .setMessage("You've used all your free consultations.\nUpgrade to Premium for unlimited access!")
                .setPositiveButton("Upgrade to Paid Membership", (dialog, which) ->
                        startActivity(new Intent(this, PaymentActivity.class)))
                .setNegativeButton("Continue with Free Account", (dialog, which) ->
                        startActivity(new Intent(this, MenuActivity.class)))
                .setCancelable(false)
                .show();
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Object photoData = result.getData().getExtras().get("data");
                    if (photoData instanceof Bitmap) {
                        capturedImage = (Bitmap) photoData;
                        imageView.setImageBitmap(capturedImage);
                        String skinType = getIntent().getStringExtra("skinType");
                        uploadImageToGoogleCloud(capturedImage, skinType);
                    } else {
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void uploadImageToGoogleCloud(Bitmap image, String skinType) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageData = stream.toByteArray();

                String imageName = "uploads/" + UUID.randomUUID() + ".jpg";
                try (InputStream credentialsStream = getAssets().open("gcs_credentials.json")) {
                    Storage storage = StorageOptions.newBuilder()
                            .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                            .build()
                            .getService();

                    BlobId blobId = BlobId.of("clearcanvasbucket2", imageName);
                    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
                    storage.create(blobInfo, imageData);

                    String publicUrl = "https://storage.googleapis.com/clearcanvasbucket2/" + imageName;
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                        sendImageToRoboflow(publicUrl, skinType);
                    });
                }

            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                Log.e("GCS_UPLOAD", "Upload failed", e);
            }
        }).start();
    }

    private void sendImageToRoboflow(String imageUrl, String skinType) {
        RoboflowRequestBody requestBody = new RoboflowRequestBody(API_KEY, imageUrl);
        Call<RoboflowResponse> call = roboflowAPI.analyzeImage(requestBody);

        call.enqueue(new Callback<RoboflowResponse>() {
            @Override
            public void onResponse(Call<RoboflowResponse> call, Response<RoboflowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().getOutputs().get(0).getGoogleGemini();
                    Intent intent = new Intent(AnalyseCameraActivity.this, ResultActivity.class);
                    intent.putExtra("skinType", skinType);
                    intent.putExtra("result", result);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e("ROBOFLOW_API", "Error body: " + response.errorBody().string());
                        } else {
                            Log.e("ROBOFLOW_API", "Empty error body");
                        }
                    } catch (IOException e) {
                        Log.e("ROBOFLOW_API", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<RoboflowResponse> call, Throwable t) {
                Toast.makeText(AnalyseCameraActivity.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
