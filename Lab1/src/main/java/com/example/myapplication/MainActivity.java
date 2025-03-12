package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup colorGroup = findViewById(R.id.colorGroup);
        EditText inputText = findViewById(R.id.inputText);
        Button okButton = findViewById(R.id.okButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        TextView resultText = findViewById(R.id.resultText);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedColorId = colorGroup.getCheckedRadioButtonId();
                String text = inputText.getText().toString().trim();

                if (selectedColorId == -1 || text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Будь ласка, введіть текст і виберіть колір!", Toast.LENGTH_SHORT).show();
                } else {
                    int color;
                    if (selectedColorId == R.id.redButton) {
                        color = Color.RED;
                    } else if (selectedColorId == R.id.blueButton) {
                        color = Color.BLUE;
                    } else if (selectedColorId == R.id.greenButton) {
                        color = Color.GREEN;
                    } else {
                        color = Color.BLACK;
                    }

                    resultText.setText(text);
                    resultText.setTextColor(color);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.setText("");
                resultText.setText("");
                colorGroup.clearCheck();
            }
        });
    }
}
