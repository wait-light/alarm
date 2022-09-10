package com.example.alarmapplication.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AlarmReceiver"
        const val ALARM_ID_KEY = "alarm_id"
        const val DEBUG = true
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (DEBUG){
            Log.e(TAG, "onReceive: ${intent?.action}   ${intent?.getLongExtra(ALARM_ID_KEY, 0)}" )
        }
        context?.startService(Intent(context, AlarmService::class.java).apply {
            putExtra(ALARM_ID_KEY, intent?.getLongExtra(ALARM_ID_KEY, 0))
            action = AlarmService.ACTION_START
        })
    }
}