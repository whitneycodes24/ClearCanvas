package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfActivity extends AppCompatActivity {

    private File generatedPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Consultation consultation = (Consultation) getIntent().getSerializableExtra("consultation");

        if (consultation != null) {
            fetchUserNameAndGenerate(consultation);
        } else {
            Toast.makeText(this, "No Consultation Result Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserNameAndGenerate(Consultation consultation) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("name");

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.exists() ? snapshot.getValue(String.class) : "User";
                String content = consultation.getResult();
                generatePdf(content, fullName, user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PdfActivity.this, "Failed to load name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePdf(String fullText, String fullName, String userEmail) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        int pageWidth = pageInfo.getPageWidth();
        int pageHeight = pageInfo.getPageHeight();
        int x = 40;
        int y = 40;
        int lineHeight = 20;
        int maxWidth = pageWidth - 80;
        int maxY = pageHeight - 60;

        Paint textPaint = new Paint();
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(13f);

        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(16f);
        boldPaint.setFakeBoldText(true);

        Paint footerPaint = new Paint();
        footerPaint.setColor(Color.GRAY);
        footerPaint.setTextSize(11f);

        Paint dividerPaint = new Paint();
        dividerPaint.setColor(Color.LTGRAY);
        dividerPaint.setStrokeWidth(2);

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        try {
            InputStream logoStream = getResources().openRawResource(R.raw.logo);
            Bitmap logo = BitmapFactory.decodeStream(logoStream);
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, false);
            canvas.drawBitmap(scaledLogo, (pageWidth - 100) / 2f, y, null);
            y += 120;

            String title = "Clear Canvas Skin Consultation Report";
            canvas.drawText(title, (pageWidth - boldPaint.measureText(title)) / 2, y, boldPaint);
            y += 20;
            canvas.drawLine(x, y, pageWidth - x, y, dividerPaint);
            y += 25;

            canvas.drawText("Name: " + fullName, x, y, textPaint); y += lineHeight;
            canvas.drawText("Email: " + userEmail, x, y, textPaint); y += lineHeight;
            canvas.drawText("Date: " + java.time.LocalDate.now(), x, y, textPaint); y += lineHeight * 2;

            canvas.drawText("AI Skin Analysis Result:", x, y, boldPaint); y += lineHeight;

            for (String line : fullText.split("\n")) {
                while (line.length() > 0) {
                    int chars = textPaint.breakText(line, true, maxWidth, null);
                    canvas.drawText(line.substring(0, chars), x, y, textPaint);
                    line = line.substring(chars);
                    y += lineHeight;

                    if (y > maxY) {
                        document.finishPage(page);
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        y = 40;
                    }
                }
            }

            canvas.drawLine(x, y, pageWidth - x, y, dividerPaint);
            y += lineHeight;
            canvas.drawText("Thank you for using Clear Canvas!", x, y, textPaint);

            document.finishPage(page);

            File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            String fileName = "SkinResult_" + System.currentTimeMillis() + ".pdf";
            generatedPdfFile = new File(dir, fileName);

            document.writeTo(new FileOutputStream(generatedPdfFile));
            document.close();

            sendEmailWithPdf(fullName, userEmail);

        } catch (Exception e) {
            document.close();
            Toast.makeText(this, "PDF generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmailWithPdf(String fullName, String email) {
        String subject = "Your Full Skin Consultation Report is Here!";
        String message = "Hi " + fullName + ",\n\n" +
                "Thank you for using Clear Canvas!\n\n" +
                "Attached is your personalized skin consultation report. We hope it helps you better understand your skincare needs and brings you that bit closer to achieving your Clear Canvas!\n\n" +
                "If you have any queries or would like to explore products tailored to your result, revisit the app again anytime!\n\n" +
                "Best wishes,\nThe Clear Canvas Team xoxo";

        Thread emailThread = new Thread(() -> {
            try {
                EmailActivity sender = new EmailActivity("clearcanvasapp@gmail.com", "ojxxeftowaoexbsn");
                sender.sendEmail(email, subject, message, generatedPdfFile);

                runOnUiThread(() -> {
                    Toast.makeText(this, "PDF Emailed Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PdfActivity.this, PdfSentActivity.class);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Email failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        emailThread.start();
    }

}
