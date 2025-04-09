package com.example.fyp_clearcanvas;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnalyseCameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final String BASE_URL = "https://detect.roboflow.com/";
    private static final String API_KEY = "MuH4XduWwn4bxx1dwmhz";

    private Button btnPicture;
    private ImageView imageView;
    private Bitmap capturedImage;

    private Retrofit retrofit;
    private RoboflowAPI roboflowAPI;

    private String uploadedImageUrl = null;  // <- Global field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_camera);

        btnPicture = findViewById(R.id.btncamera);
        imageView = findViewById(R.id.imageview1);
        TextView resultTextView = findViewById(R.id.resultTextView);

        String skinType = getIntent().getStringExtra("skinType");
        Log.d("AnalyseCameraActivity", "Received skinType: " + skinType);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        roboflowAPI = retrofit.create(RoboflowAPI.class);

        btnPicture.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    capturedImage = photo;
                    imageView.setImageBitmap(photo);

                    String skinType = getIntent().getStringExtra("skinType");
                    Log.d("AnalyseCameraActivity", "Received skinType: " + skinType);

                    uploadImageToGoogleCloud(photo, skinType);
                }
            });

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToGoogleCloud(Bitmap image, String skinType) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageData = byteArrayOutputStream.toByteArray();

                InputStream credentialsStream = getResources().openRawResource(R.raw.google_service);

                Storage storage = StorageOptions.newBuilder()
                        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                        .build()
                        .getService();

                String imageName = "uploads/" + UUID.randomUUID().toString() + ".jpg";
                BlobId blobId = BlobId.of("clearcanvasbucket2", imageName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();

                Blob blob = storage.create(blobInfo, imageData);

                //public URL
                uploadedImageUrl = "https://storage.googleapis.com/clearcanvasbucket2/" + imageName;

                Log.d("GoogleCloud", "Image Uploaded Successfully: " + uploadedImageUrl);

                runOnUiThread(() -> {
                    Toast.makeText(AnalyseCameraActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    sendImageToRoboflow(uploadedImageUrl, skinType);
                });

            } catch (IOException e) {
                Log.e("GoogleCloud", "Image upload failed", e);
                runOnUiThread(() -> Toast.makeText(AnalyseCameraActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void sendImageToRoboflow(String imageUrl, String skinType) {
        Log.d("Roboflow", "Sending image URL to Roboflow: " + imageUrl);

        RoboflowRequestBody requestBody = new RoboflowRequestBody(API_KEY, imageUrl);

        Call<RoboflowResponse> call = roboflowAPI.analyzeImage(requestBody);
        call.enqueue(new Callback<RoboflowResponse>() {
            @Override
            public void onResponse(Call<RoboflowResponse> call, retrofit2.Response<RoboflowResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String googleGemini = response.body().getOutputs().get(0).getGoogleGemini();
                        Log.d("googleGemini", googleGemini);

                        Intent intent = new Intent(AnalyseCameraActivity.this, ResultActivity.class);
                        intent.putExtra("skinType", skinType);
                        intent.putExtra("result", googleGemini);
                        intent.putExtra("imageUrl", uploadedImageUrl);  // Pass uploaded image URL
                        startActivity(intent);

                    } else {
                        Log.e("RoboflowAPI", "Error Response Code: " + response.code());
                        Log.e("RoboflowAPI", "Error Body: " + (response.errorBody() != null ? response.errorBody().string() : "No error body"));
                        Toast.makeText(AnalyseCameraActivity.this, "API Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("RoboflowAPI", "Error parsing response: ", e);
                }
            }

            @Override
            public void onFailure(Call<RoboflowResponse> call, Throwable t) {
                Log.e("RoboflowAPI", "Request Failed: ", t);
                Toast.makeText(AnalyseCameraActivity.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
