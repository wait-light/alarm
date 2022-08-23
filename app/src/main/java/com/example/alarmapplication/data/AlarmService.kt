package com.example.alarmapplication.data

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AlarmService : Service() {
    companion object{
        private const val TAG = "AlarmService"
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: xxxxx${this.hashCode()}", )
    }
}