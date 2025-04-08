package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StorageActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayList<String> listItems;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        dbHelper = new DatabaseHelper(this);
        ListView listView = findViewById(R.id.list_view);
        Button btnBack = findViewById(R.id.btn_back);
        Button btnDeleteAll = findViewById(R.id.btn_delete_all);

        listItems = new ArrayList<>();
        adapter = new CustomAdapter(this, listItems);
        listView.setAdapter(adapter);

        loadData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllData();
                loadData();
                Toast.makeText(StorageActivity.this, "Всі дані видалено", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        listItems.clear();
        Cursor cursor = dbHelper.getAllData();
        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(1);
                listItems.add(text);
            } while (cursor.moveToNext());
        } else {
            listItems.add("Сховище пусте");
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        private final ArrayList<Integer> colors;

        public CustomAdapter(StorageActivity context, ArrayList<String> items) {
            super(context, android.R.layout.simple_list_item_1, items);
            colors = new ArrayList<>();
            Cursor cursor = dbHelper.getAllData();
            if (cursor.moveToFirst()) {
                do {
                    colors.add(cursor.getInt(2));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            if (position < colors.size()) {
                textView.setTextColor(colors.get(position));
            } else {
                textView.setTextColor(Color.BLACK);
            }
            return textView;
        }
    }
}