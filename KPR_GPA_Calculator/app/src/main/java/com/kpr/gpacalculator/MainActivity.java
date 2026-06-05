package com.kpr.gpacalculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSgpa = findViewById(R.id.btnSgpa);
        Button btnCgpa = findViewById(R.id.btnCgpa);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnSgpa.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SgpaActivity.class));
        });

        btnCgpa.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CgpaActivity.class));
        });

        btnAbout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
    }
}
