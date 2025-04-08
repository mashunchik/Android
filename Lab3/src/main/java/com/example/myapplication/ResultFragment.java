package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ResultFragment extends Fragment {
    private TextView resultTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        resultTextView = view.findViewById(R.id.result_text);
        return view;
    }

    public void displayResult(String text, int color) {
        resultTextView.setText(text);
        resultTextView.setTextColor(color);
    }

    public void clearResult() {
        resultTextView.setText("");
    }
}