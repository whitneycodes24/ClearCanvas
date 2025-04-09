package com.example.fyp_clearcanvas;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private LineChart graph;
    private List<Consultation> consultationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graph = findViewById(R.id.graph);

        consultationList = (List<Consultation>) getIntent().getSerializableExtra("consultations");

        if (consultationList != null && !consultationList.isEmpty()) {
            showGraph();
        }
    }

    private void showGraph() {
        if (consultationList == null || consultationList.isEmpty()) {
            graph.setNoDataText("You Have No Consultations.\nGo Analyse Your Skin!");
            graph.setNoDataTextColor(getResources().getColor(android.R.color.black));
            graph.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
            graph.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        for (int i = 0; i < consultationList.size(); i++) {
            Consultation consultation = consultationList.get(i);
            entries.add(new Entry(i, i));
            String dateStr = new SimpleDateFormat("EEE dd MMM", Locale.getDefault())
                    .format(new Date(consultation.getTimestamp()));
            xAxisLabels.add(dateStr);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Consultations");
        dataSet.setCircleRadius(6f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        graph.setData(lineData);

        Description description = new Description();
        description.setText("Progress Timeline");
        graph.setDescription(description);

        graph.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < xAxisLabels.size()) {
                    return xAxisLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        graph.getXAxis().setGranularity(1f);
        graph.getXAxis().setGranularityEnabled(true);

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

        graph.invalidate();
    }
}
