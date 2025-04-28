package com.example.lab5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab5.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private lateinit var stepsTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var addStepButton: Button
    private lateinit var showStatsButton: Button
    private var initialSteps = 0
    private var currentSteps = 0
    private var isUsingAccelerometer = false
    private var lastYAcceleration = 0f
    private var accelerationThreshold = 0.1f
    private var lastUpdateTime = 0L
    private val stepDetectionInterval = 300L
    private var isTraining = false
    private var trainingStartTime: Long = 0L
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stepsTextView = findViewById(R.id.steps_text_view)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        resetButton = findViewById(R.id.reset_button)
        addStepButton = findViewById(R.id.add_step_button)
        showStatsButton = findViewById(R.id.show_stats_button)

        sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (stepCounterSensor != null) {
            stepsTextView.text = getString(R.string.steps_count, 0)
            Log.d("StepCounter", "Using STEP_COUNTER sensor")
        } else {
            if (accelerometerSensor != null) {
                isUsingAccelerometer = true
                stepsTextView.text = getString(R.string.steps_count, 0)
                Log.d("StepCounter", "Using ACCELEROMETER sensor")
            } else {
                stepsTextView.text = getString(R.string.sensor_not_found)
                resetButton.isEnabled = false
                startButton.isEnabled = false
                stopButton.isEnabled = false
                Log.d("StepCounter", "No sensors available")
            }
        }

        addStepButton.isEnabled = true

        startButton.setOnClickListener {
            if (!isTraining) {
                isTraining = true
                trainingStartTime = System.currentTimeMillis()
                initialSteps = currentSteps
                startButton.isEnabled = false
                stopButton.isEnabled = true
                resetButton.isEnabled = false
                Log.d("StepCounter", "Training started at $trainingStartTime")
            }
        }

        stopButton.setOnClickListener {
            if (isTraining) {
                isTraining = false
                val trainingEndTime = System.currentTimeMillis()
                val duration = (trainingEndTime - trainingStartTime) / 1000 // Тривалість у секундах
                val stepsDuringTraining = currentSteps - initialSteps
                saveTrainingData(stepsDuringTraining, trainingStartTime, duration)
                startButton.isEnabled = true
                stopButton.isEnabled = false
                resetButton.isEnabled = true
                Log.d("StepCounter", "Training stopped. Steps: $stepsDuringTraining, Duration: $duration seconds")
            }
        }

        resetButton.setOnClickListener {
            initialSteps = currentSteps
            updateStepsDisplay()
        }

        addStepButton.setOnClickListener {
            currentSteps++
            updateStepsDisplay()
            Log.d("StepCounter", "Manually added step. Total steps: $currentSteps")
        }

        showStatsButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        stopButton.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        if (isUsingAccelerometer) {
            accelerometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        } else {
            stepCounterSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    currentSteps = it.values[0].toInt()
                    updateStepsDisplay()
                    Log.d("StepCounter", "Step counter updated: $currentSteps")
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    if (!isTraining) return
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdateTime < stepDetectionInterval) {
                        return
                    }

                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    val yAcceleration = y
                    val delta = yAcceleration - lastYAcceleration

                    Log.d("StepCounter", "Accelerometer: x=$x, y=$y, z=$z, yAcceleration=$yAcceleration, delta=$delta")

                    if (abs(delta) > accelerationThreshold) {
                        currentSteps++
                        lastUpdateTime = currentTime
                        updateStepsDisplay()
                        Log.d("StepCounter", "Step detected via accelerometer. Total steps: $currentSteps, delta: $delta")
                    }
                    lastYAcceleration = yAcceleration
                }
                else -> {
                    Log.d("StepCounter", "Unsupported sensor type: ${it.sensor.type}")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun updateStepsDisplay() {
        val stepsToShow = if (initialSteps == 0) {
            currentSteps
        } else {
            currentSteps - initialSteps
        }
        stepsTextView.text = getString(R.string.steps_count, stepsToShow)
    }

    private fun saveTrainingData(steps: Int, startTime: Long, duration: Long) {
        val editor = sharedPreferences.edit()
        val trainingCount = sharedPreferences.getInt("training_count", 0)
        val newTrainingId = trainingCount + 1

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startDate = dateFormat.format(Date(startTime))

        editor.putString("training_${newTrainingId}_date", startDate)
        editor.putInt("training_${newTrainingId}_steps", steps)
        editor.putLong("training_${newTrainingId}_duration", duration)
        editor.putInt("training_count", newTrainingId)
        editor.apply()
    }
}