package com.alexmercerind.strider.ui

import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.alexmercerind.strider.ui.theme.StriderTheme
import com.alexmercerind.strider.utils.Constants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = getSystemService(Service.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = service.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetectorSensor = service.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        Log.d(Constants.LOG_TAG, stepCounterSensor.toString())
        Log.d(Constants.LOG_TAG, stepDetectorSensor.toString())
        service.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                Log.d(Constants.LOG_TAG, "TYPE_STEP_COUNTER: event=${event?.values?.toList()}")
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(Constants.LOG_TAG, "TYPE_STEP_COUNTER: accuracy=$accuracy")
            }
        }, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
        service.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                Log.d(Constants.LOG_TAG, "TYPE_STEP_DETECTOR: event=${event?.values?.toList()}")
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(Constants.LOG_TAG, "TYPE_STEP_DETECTOR: accuracy=$accuracy")
            }
        }, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST)

        setContent {
            StriderTheme {
                Surface {

                }
            }
        }
    }
}
