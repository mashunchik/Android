package com.example.lab2
import com.example.lab2.R

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val inputFragment = InputFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, inputFragment)
                .commit()
        }
    }
}