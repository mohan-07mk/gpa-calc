package com.kpr.gpacalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CgpaActivity extends AppCompatActivity {

    private LinearLayout semestersContainer;
    private MaterialCardView cardResult;
    private TextView tvCgpa, tvPercentage;
    private MaterialButton btnCalculate, btnReset, btnCopy, btnShare;

    private List<EditText> editTexts = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgpa);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("GpaHistory", MODE_PRIVATE);

        initViews();
        createSemesterViews();
        setupListeners();
        loadHistory();
    }

    private void initViews() {
        semestersContainer = findViewById(R.id.semestersContainer);
        cardResult = findViewById(R.id.cardResult);
        tvCgpa = findViewById(R.id.tvCgpa);
        tvPercentage = findViewById(R.id.tvPercentage);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);
        btnCopy = findViewById(R.id.btnCopy);
        btnShare = findViewById(R.id.btnShare);
    }

    private void createSemesterViews() {
        for (int i = 1; i <= 8; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 0, 0, 32);

            TextView tvName = new TextView(this);
            tvName.setText("Semester " + i + " SGPA");
            tvName.setTextSize(16);
            tvName.setPadding(0, 0, 0, 8);

            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setHint("Enter SGPA (e.g., 8.5)");
            editText.setBackgroundResource(android.R.drawable.edit_text);
            editTexts.add(editText);

            row.addView(tvName);
            row.addView(editText);

            semestersContainer.addView(row);
        }
    }

    private void setupListeners() {
        btnCalculate.setOnClickListener(v -> calculateCgpa());
        btnReset.setOnClickListener(v -> resetFields());

        btnCopy.setOnClickListener(v -> {
            String result = "My Overall CGPA is " + tvCgpa.getText().toString().replace("CGPA: ", "") + 
                            "\n" + tvPercentage.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("CGPA Result", result);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(CgpaActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            String result = "My Overall CGPA is " + tvCgpa.getText().toString().replace("CGPA: ", "") + 
                            "\n" + tvPercentage.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, result);
            startActivity(Intent.createChooser(intent, "Share via"));
        });
    }

    private void calculateCgpa() {
        double totalSgpa = 0;
        int count = 0;
        
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < editTexts.size(); i++) {
            String text = editTexts.get(i).getText().toString().trim();
            if (!text.isEmpty()) {
                try {
                    double sgpa = Double.parseDouble(text);
                    if (sgpa < 0 || sgpa > 10) {
                        editTexts.get(i).setError("Enter a valid SGPA (0-10)");
                        return;
                    }
                    totalSgpa += sgpa;
                    count++;
                    editor.putString("sem_" + (i + 1), text);
                } catch (NumberFormatException e) {
                    editTexts.get(i).setError("Invalid number");
                    return;
                }
            } else {
                editor.remove("sem_" + (i + 1));
            }
        }
        
        editor.apply();

        if (count == 0) {
            Toast.makeText(this, "Please enter at least one SGPA", Toast.LENGTH_SHORT).show();
            return;
        }

        double cgpa = totalSgpa / count;
        double percentage = (cgpa - 0.5) * 10;
        if (percentage < 0) percentage = 0;

        tvCgpa.setText(String.format("CGPA: %.2f", cgpa));
        tvPercentage.setText(String.format("Percentage: %.2f%%", percentage));

        cardResult.setVisibility(View.VISIBLE);
    }
    
    private void loadHistory() {
        for (int i = 0; i < editTexts.size(); i++) {
            String savedValue = sharedPreferences.getString("sem_" + (i + 1), "");
            editTexts.get(i).setText(savedValue);
        }
    }

    private void resetFields() {
        for (EditText editText : editTexts) {
            editText.setText("");
            editText.setError(null);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
}
