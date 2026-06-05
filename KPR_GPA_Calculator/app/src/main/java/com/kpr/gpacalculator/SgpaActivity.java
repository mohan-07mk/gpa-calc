package com.kpr.gpacalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SgpaActivity extends AppCompatActivity {

    private LinearLayout subjectsContainer;
    private MaterialCardView cardResult;
    private TextView tvTotalCredits, tvQualityPoints, tvSgpa, tvPercentage;
    private MaterialButton btnCalculate, btnReset, btnCopy, btnShare;

    private List<Subject> subjects = new ArrayList<>();
    private List<Spinner> spinners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgpa);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        loadSubjects();
        createSubjectViews();
        setupListeners();
    }

    private void initViews() {
        subjectsContainer = findViewById(R.id.subjectsContainer);
        cardResult = findViewById(R.id.cardResult);
        tvTotalCredits = findViewById(R.id.tvTotalCredits);
        tvQualityPoints = findViewById(R.id.tvQualityPoints);
        tvSgpa = findViewById(R.id.tvSgpa);
        tvPercentage = findViewById(R.id.tvPercentage);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);
        btnCopy = findViewById(R.id.btnCopy);
        btnShare = findViewById(R.id.btnShare);
    }

    private void loadSubjects() {
        subjects.add(new Subject("Java Programming", 3));
        subjects.add(new Subject("Air Pollution and Control", 3));
        subjects.add(new Subject("Electromagnetic Fields and Waveguides", 3));
        subjects.add(new Subject("Digital Signal Processing", 3));
        subjects.add(new Subject("Digital Communication", 2));
        subjects.add(new Subject("Microprocessors and Microcontrollers", 4));
        subjects.add(new Subject("Analog and Digital Communication Laboratory", 2));
        subjects.add(new Subject("Digital Signal Processing Laboratory", 2));
        subjects.add(new Subject("Design Studio II", 1));
        subjects.add(new Subject("Soft skills", 1));
    }

    private void createSubjectViews() {
        String[] grades = getResources().getStringArray(R.array.grade_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 0, 0, 32);

            TextView tvName = new TextView(this);
            tvName.setText((i + 1) + ". " + subject.name + " (" + subject.credits + " Credits)");
            tvName.setTextSize(16);
            tvName.setPadding(0, 0, 0, 8);

            Spinner spinner = new Spinner(this);
            spinner.setAdapter(adapter);
            spinner.setBackgroundResource(android.R.drawable.btn_dropdown);
            spinners.add(spinner);

            row.addView(tvName);
            row.addView(spinner);

            subjectsContainer.addView(row);
        }
    }

    private void setupListeners() {
        btnCalculate.setOnClickListener(v -> calculateSgpa());
        btnReset.setOnClickListener(v -> resetFields());

        btnCopy.setOnClickListener(v -> {
            String result = "My Semester 4 SGPA is " + tvSgpa.getText().toString().replace("SGPA: ", "") + 
                            "\n" + tvPercentage.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("SGPA Result", result);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SgpaActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            String result = "My Semester 4 SGPA is " + tvSgpa.getText().toString().replace("SGPA: ", "") + 
                            "\n" + tvPercentage.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, result);
            startActivity(Intent.createChooser(intent, "Share via"));
        });
    }

    private void calculateSgpa() {
        int totalCredits = 0;
        int totalQualityPoints = 0;

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            String grade = spinners.get(i).getSelectedItem().toString();
            int gradePoint = getGradePoint(grade);

            totalCredits += subject.credits;
            totalQualityPoints += (subject.credits * gradePoint);
        }

        if (totalCredits == 0) return;

        double sgpa = (double) totalQualityPoints / totalCredits;
        double percentage = (sgpa - 0.5) * 10;
        if (percentage < 0) percentage = 0;

        tvTotalCredits.setText("Total Credits: " + totalCredits);
        tvQualityPoints.setText("Total Quality Points: " + totalQualityPoints);
        tvSgpa.setText(String.format("SGPA: %.2f", sgpa));
        tvPercentage.setText(String.format("Percentage: %.2f%%", percentage));

        cardResult.setVisibility(View.VISIBLE);
    }

    private int getGradePoint(String grade) {
        switch (grade) {
            case "O": return 10;
            case "A+": return 9;
            case "A": return 8;
            case "B+": return 7;
            case "B": return 6;
            case "RA": return 0;
            default: return 0;
        }
    }

    private void resetFields() {
        for (Spinner spinner : spinners) {
            spinner.setSelection(0);
        }
        cardResult.setVisibility(View.GONE);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class Subject {
        String name;
        int credits;

        Subject(String name, int credits) {
            this.name = name;
            this.credits = credits;
        }
    }
}
