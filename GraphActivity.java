package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private RecyclerView imageRecyclerView;
    private ConsultationImageAdapter imageAdapter;
    private LineChart graph;
    private List<Consultation> consultationList;
    private TextView comparisonMessage;
    private ProgressBar progressBar;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.beige));
        }


        graph = findViewById(R.id.graph);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        comparisonMessage = findViewById(R.id.comparisonMessage);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fixOldConsultationsThenLoadThem();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(GraphActivity.this, MenuActivity.class));
            finish();
        });

    }


    private void showGraph() {
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        for (int i = 0; i < consultationList.size(); i++) {
            Consultation consultation = consultationList.get(i);
            entries.add(new Entry(i, consultation.getAcneRating()));
            String dateStr = new SimpleDateFormat("dd MMM", Locale.getDefault())
                    .format(new Date(consultation.getTimestamp()));
            xAxisLabels.add(dateStr);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Acne Severity Rating");
        dataSet.setColor(getResources().getColor(R.color.primary_color));
        dataSet.setCircleColor(getResources().getColor(R.color.primary_color));
        dataSet.setCircleRadius(6f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setLineWidth(3f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.primary_color));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);   //smooth lines

        LineData lineData = new LineData(dataSet);
        graph.setData(lineData);

        Description description = new Description();
        description.setText("Your Progress To Your Clear Canvas Over Time");
        description.setTextColor(Color.DKGRAY);
        description.setTextSize(12f);
        graph.setDescription(description);

        XAxis xAxis = graph.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < xAxisLabels.size()) ? xAxisLabels.get(index) : "";
            }
        });

        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value <= 3) return "Clear";
                else if (value <= 6) return "Mild";
                else return "Severe";
            }
        });

        graph.getAxisRight().setEnabled(false);
        graph.getLegend().setEnabled(false);

        graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                Consultation selected = consultationList.get(index);
                Intent intent = new Intent(GraphActivity.this, ConsultationViewActivity.class);
                intent.putExtra("result", selected.getResult());
                intent.putExtra("acneType", selected.getAcneType());
                intent.putExtra("skinType", selected.getSkinType());
                intent.putExtra("date", selected.getDate());
                intent.putExtra("imageUrl", selected.getImageUrl());
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {}
        });

        graph.animateY(1000);
        graph.invalidate();
    }

    private void showComparisonMessage() {
        if (consultationList.size() < 2) {
            comparisonMessage.setText("Take More Consultations To See Your Acne Progress!");
            return;
        }

        float last = consultationList.get(consultationList.size() - 1).getAcneRating();
        float previous = consultationList.get(consultationList.size() - 2).getAcneRating();

        if (previous == 0) {
            comparisonMessage.setText("Not Enough Data To Compare Yet.");
            return;
        }

        float change = ((last - previous) / previous) * 100;

        if (change < 0) {
            comparisonMessage.setText(Html.fromHtml(String.format(Locale.getDefault(),
                            "Good News! Your Acne Severity Improved by <b>%.1f%%</b>!", Math.abs(change)),
                    Html.FROM_HTML_MODE_LEGACY));
            comparisonMessage.setTextColor(Color.parseColor("#4CAF50"));
        } else if (change > 0) {
            comparisonMessage.setText(Html.fromHtml(String.format(Locale.getDefault(),
                            "Your Acne Severity Worsened by <b>%.1f%%</b>. Stay Consistent!", change),
                    Html.FROM_HTML_MODE_LEGACY));
            comparisonMessage.setTextColor(Color.parseColor("#F44336"));
        } else {
            comparisonMessage.setText("No Change In Acne Severity! Keep Tracking.");
            comparisonMessage.setTextColor(Color.parseColor("#FFC107"));
        }
    }

    private void showBestImprovementMessage() {
        if (consultationList.size() < 2) return;

        float bestImprovement = 0f;
        int startIdx = -1;
        int endIdx = -1;

        for (int i = 0; i < consultationList.size() - 1; i++) {  //looks through all but most recent
            for (int j = i + 1; j < consultationList.size(); j++) {  //past to present
                float start = consultationList.get(i).getAcneRating();
                float end = consultationList.get(j).getAcneRating();
                float improvement = start - end;
                if (improvement > bestImprovement) {
                    bestImprovement = improvement;
                    startIdx = i;
                    endIdx = j;
                }
            }
        }

        if (bestImprovement > 0 && startIdx != -1 && endIdx != -1) {
            float percentageImprovement = (bestImprovement / consultationList.get(startIdx).getAcneRating()) * 100f;
            comparisonMessage.append(Html.fromHtml(String.format(Locale.getDefault(),
                    "<br><br> Your Best Improvement: <b>%.1f%%</b> between %s and %s!",
                    percentageImprovement,
                    new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(consultationList.get(startIdx).getTimestamp())),
                    new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(consultationList.get(endIdx).getTimestamp()))
            ), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private void fixOldConsultationsThenLoadThem() {   //gets old consultations
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference consultationsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("consultations");

        consultationsRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful() && task.getResult().exists()) {  //if score is missing it goes back and gets it
                consultationList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Consultation consultation = snapshot.getValue(Consultation.class);
                    if (consultation != null) {
                        if (consultation.getAcneRating() == 0 && consultation.getResult() != null) {
                            int correctedRating = extractAcneRating(consultation.getResult());
                            snapshot.getRef().child("acneRating").setValue(correctedRating);
                            consultation.setAcneRating(correctedRating);
                        }
                        consultationList.add(consultation);
                    }
                }

                if (!consultationList.isEmpty()) {
                    imageAdapter = new ConsultationImageAdapter(consultationList, this);
                    imageRecyclerView.setAdapter(imageAdapter);
                    showGraph();
                    showComparisonMessage();
                    showBestImprovementMessage();
                } else {
                    graph.setNoDataText("You Have No Consultations.\nGo Analyse Your Skin!");
                    graph.setNoDataTextColor(Color.BLACK);
                    graph.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
                }
            } else {
                Toast.makeText(this, "Failed To Load Consultations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int extractAcneRating(String result) {
        if (result == null || result.trim().isEmpty()) return 0;

        try {
            String[] lines = result.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.toLowerCase(Locale.ROOT).startsWith("acne score")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        String scorePart = parts[1].trim();
                        scorePart = scorePart.replaceAll("[^0-9]", "");
                        if (!scorePart.isEmpty()) {
                            return Integer.parseInt(scorePart);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
