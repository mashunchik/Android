package com.example.lab5

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab5.R

class StatsActivity : AppCompatActivity() {

    private lateinit var statsTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        statsTextView = findViewById(R.id.stats_text_view)
        sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)

        displayStats()
    }

    private fun displayStats() {
        val trainingCount = sharedPreferences.getInt("training_count", 0)
        if (trainingCount == 0) {
            statsTextView.text = getString(R.string.no_trainings)
            return
        }

        val statsBuilder = StringBuilder()
        for (i in 1..trainingCount) {
            val date = sharedPreferences.getString("training_${i}_date", "N/A") ?: "N/A"
            val steps = sharedPreferences.getInt("training_${i}_steps", 0)
            val duration = sharedPreferences.getLong("training_${i}_duration", 0) // у секундах
            val stepsPerMinute = if (duration > 0) (steps * 60 / duration).toFloat() else 0f

            statsBuilder.append("Тренування $i\n")
            statsBuilder.append("Дата: $date\n")
            statsBuilder.append("Кількість кроків: $steps\n")
            statsBuilder.append("Тривалість: $duration секунд\n")
            statsBuilder.append("Швидкість: %.2f кроків/хв\n\n".format(stepsPerMinute))
        }

        statsTextView.text = statsBuilder.toString()
    }
}