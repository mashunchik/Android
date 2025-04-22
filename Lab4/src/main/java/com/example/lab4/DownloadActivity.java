package com.example.lab4;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {
    private EditText urlEditText;
    private RadioButton audioRadioButton;
    private long downloadID = 0;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        urlEditText = findViewById(R.id.urlEditText);
        Button downloadButton = findViewById(R.id.downloadButton);
        audioRadioButton = findViewById(R.id.audioRadioButton);
        RadioButton videoRadioButton = findViewById(R.id.videoRadioButton);

        audioRadioButton.setChecked(true);

        downloadButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            if (!url.isEmpty()) {
                Log.d("DownloadActivity", "Starting download for URL: " + url);
                downloadFile(url);
            } else {
                Toast.makeText(this, "Будь ласка, введіть URL", Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onDownloadComplete, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(onDownloadComplete, filter);
        }
    }

    private void downloadFile(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle("Завантаження медіа")
                .setDescription("Завантаження файлу...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        String fileName = audioRadioButton.isChecked() ? "downloaded_media.mp3" : "downloaded_media.mp4";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        request.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
        Log.d("DownloadActivity", "Download started with ID: " + downloadID);
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @SuppressLint("Range")
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Log.d("DownloadActivity", "Download completed for ID: " + id);
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadID);
                try (Cursor cursor = downloadManager.query(query)) {
                    if (cursor.moveToFirst()) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (uriString == null || uriString.isEmpty()) {
                                Log.w("DownloadActivity", "COLUMN_LOCAL_URI is null or empty, constructing fallback URI");
                                String fileName = audioRadioButton.isChecked() ? "downloaded_media.mp3" : "downloaded_media.mp4";
                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                                uriString = Uri.fromFile(file).toString();
                            }

                            Uri uri = Uri.parse(uriString);
                            Log.d("DownloadActivity", "Final URI to pass: " + uriString);

                            File file = new File(uri.getPath());
                            if (file.exists()) {
                                Intent newIntent = audioRadioButton.isChecked() ?
                                        new Intent(DownloadActivity.this, AudioPlayerActivity.class) :
                                        new Intent(DownloadActivity.this, VideoPlayerActivity.class);
                                newIntent.putExtra("uri", uri.toString());
                                Log.d("DownloadActivity", "Starting activity with URI: " + uri.toString());
                                startActivity(newIntent);
                            } else {
                                Log.e("DownloadActivity", "File does not exist at: " + uri.getPath());
                                Toast.makeText(context, "Файл не знайдено: " + uriString, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            Log.e("DownloadActivity", "Download failed with status: " + status + ", reason: " + reason);
                            Toast.makeText(context, "Завантаження не вдалося. Причина: " + reason, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("DownloadActivity", "Cursor is empty, no download info found");
                        Toast.makeText(context, "Завантаження не вдалося: інформація відсутня", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}