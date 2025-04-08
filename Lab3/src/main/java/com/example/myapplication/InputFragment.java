package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {
    private EditText editText;
    private RadioGroup radioGroup;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        editText = view.findViewById(R.id.edit_text);
        Button okButton = view.findViewById(R.id.ok_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        radioGroup = view.findViewById(R.id.radio_group);
        dbHelper = new DatabaseHelper(requireContext());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                if (text.isEmpty() || checkedRadioButtonId == -1) {
                    Toast.makeText(getContext(), "Будь ласка, введіть текст і виберіть колір", Toast.LENGTH_SHORT).show();
                    return;
                }

                int color = getColorFromRadioButton(checkedRadioButtonId);
                ResultFragment resultFragment = (ResultFragment) getParentFragmentManager()
                        .findFragmentById(R.id.result_fragment_container);
                if (resultFragment != null) {
                    resultFragment.displayResult(text, color);
                    long newRowId = dbHelper.insertData(text, color);
                    if (newRowId != -1) {
                        Toast.makeText(getContext(), "Дані успішно збережено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Помилка збереження", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                radioGroup.clearCheck();
                ResultFragment resultFragment = (ResultFragment) getParentFragmentManager()
                        .findFragmentById(R.id.result_fragment_container);
                if (resultFragment != null) {
                    resultFragment.clearResult();
                }
            }
        });

        return view;
    }

    private int getColorFromRadioButton(int checkedId) {
        if (checkedId == R.id.radio_red) {
            return Color.RED;
        } else if (checkedId == R.id.radio_blue) {
            return Color.BLUE;
        } else if (checkedId == R.id.radio_green) {
            return Color.GREEN;
        } else {
            return Color.BLACK;
        }
    }

    public void clearInput() {
        editText.setText("");
        radioGroup.clearCheck();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}