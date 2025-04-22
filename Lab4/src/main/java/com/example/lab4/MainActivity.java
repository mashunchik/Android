package com.example.lab4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;

    private final ActivityResultLauncher<String> pickAudioLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
                    intent.putExtra("uri", uri.toString()); // передаємо URI
                    startActivity(intent);
                }
            }
    );

    private final ActivityResultLauncher<String> pickVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("uri", uri.toString());
                    startActivity(intent);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

      // файл з raw
        Button pickAudioFileButton = findViewById(R.id.playLocalAudioButton); // вибір файлу
        Button playLocalVideoButton = findViewById(R.id.playLocalVideoButton); // відео
        Button downloadMediaButton = findViewById(R.id.downloadMediaButton); // перехід до DownloadActivity



        pickAudioFileButton.setOnClickListener(v -> pickAudioLauncher.launch("audio/*"));

        playLocalVideoButton.setOnClickListener(v -> pickVideoLauncher.launch("video/*"));

        downloadMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(intent);
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}