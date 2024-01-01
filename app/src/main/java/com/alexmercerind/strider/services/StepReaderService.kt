package com.alexmercerind.strider.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.alexmercerind.strider.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StepReaderService : LifecycleService() {
    companion object {
        const val FOREGROUND_ID = 0x000A

        const val NOTIFICATION_CHANNEL_ID = "STEP_READER_SERVICE"
        const val NOTIFICATION_CHANNEL_NAME = "Step Reader Service"

        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var notificationManager: NotificationManager

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.values?.firstOrNull() == 1.0F) {
                // TODO: Missing implementation.
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun start() {
        val stopIntent = Intent(this, StepReaderService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopAction = NotificationCompat.Action(
            R.drawable.baseline_stop_24,
            getString(R.string.service_stop),
            stopPendingIntent
        )
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_directions_walk_24)
            .setContentTitle(getString(R.string.notification_content_title))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setShowWhen(false)
            .addAction(stopAction)
            .build()
        startForeground(FOREGROUND_ID, notification)
    }

    private fun stop() {
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Save references to SensorManager & NotificationManager.
        sensorManager = getSystemService(SensorManager::class.java)
        notificationManager = getSystemService(NotificationManager::class.java)

        // Register listeners.
        try {
            val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            sensorManager.registerListener(
                listener,
                stepCounterSensor!!,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } catch (e: Throwable) {
            e.printStackTrace()

            Toast.makeText(this, R.string.sensor_not_found, Toast.LENGTH_LONG).show()

            // Sensor not available i.e. stop service.
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000L)
                stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister listeners.
        try {
            sensorManager.unregisterListener(listener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Save references to SensorManager & NotificationManager.
        sensorManager = getSystemService(SensorManager::class.java)
        notificationManager = getSystemService(NotificationManager::class.java)

        // Check for Manifest.permission.POST_NOTIFICATIONS.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Create notification channel.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_MIN
                    )
                )
            }

            when (intent?.action) {
                ACTION_START -> start()
                ACTION_STOP -> stop()
            }
        }
        return START_STICKY
    }
}

