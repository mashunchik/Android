package com.example.lab4;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;


import androidx.appcompat.app.AppCompatActivity;


import java.io.File;

public class AudioPlayerActivity extends AppCompatActivity {
    private ExoPlayer exoPlayer;
    private SeekBar seekBar;
    private TextView timerText;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isTracking = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        Button playButton = findViewById(R.id.playButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        Button stopButton = findViewById(R.id.stopButton);
        seekBar = findViewById(R.id.seekBar);
        timerText = findViewById(R.id.timerText);

        exoPlayer = new ExoPlayer.Builder(this).build();

        String uriString = getIntent().getStringExtra("uri");
        if (uriString == null) {
            Toast.makeText(this, "URI не передано", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(uriString));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    updateSeekBar();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                }
                updateTimer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
            }
        });

        playButton.setOnClickListener(v -> exoPlayer.play());
        pauseButton.setOnClickListener(v -> exoPlayer.pause());
        stopButton.setOnClickListener(v -> {
            exoPlayer.stop();
            exoPlayer.seekTo(0);
            seekBar.setProgress(0);
            timerText.setText("00:00");
        });
    }

    private void updateSeekBar() {
        seekBar.setMax((int) exoPlayer.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isTracking && exoPlayer.isPlaying()) {
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    updateTimer((int) exoPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void updateTimer(int progress) {
        int minutes = (progress / 1000) / 60;
        int seconds = (progress / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
        handler.removeCallbacksAndMessages(null);
    }
}