package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;

    private ImageView photoPreview;
    private EditText photoNote;
    private Uri imageUri;
    private Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        photoPreview = findViewById(R.id.photoPreview);
        photoNote = findViewById(R.id.picNote);
        Button btnCamera = findViewById(R.id.btnCameraPic);
        Button btnGallery = findViewById(R.id.btnGalleryPic);
        Button btnSave = findViewById(R.id.btnSavePic);

        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> savePhotoToFirebase());
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                bitmapImage = (Bitmap) data.getExtras().get("data");
                photoPreview.setImageBitmap(bitmapImage);
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                photoPreview.setImageURI(imageUri);
            }
        }
    }

    private void savePhotoToFirebase() {
        if (bitmapImage == null && imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = photoNote.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String entryId = UUID.randomUUID().toString();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("users/" + userId + "/timeline/" + entryId + ".jpg");

        if (bitmapImage != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveEntryToDatabase(userId, entryId, uri.toString(), note, timestamp);
                });
            });
        } else {
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveEntryToDatabase(userId, entryId, uri.toString(), note, timestamp);
                });
            });
        }
    }

    private void saveEntryToDatabase(String userId, String entryId, String imageUrl, String note, String timestamp) {
        PhotoItem entry = new PhotoItem(entryId, imageUrl, note, timestamp);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("timeline")
                .child(entryId)
                .setValue(entry)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show();
                    photoNote.setText("");
                    photoPreview.setImageDrawable(null);
                    bitmapImage = null;
                    imageUri = null;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show();
                });
    }
}
