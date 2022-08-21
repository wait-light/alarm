package com.example.alarmapplication.data

import android.content.Context
import java.time.LocalTime
import javax.inject.Inject

data class Alarm(
    val id: Int,
    val localTime: LocalTime,
    val repeat: String,
    val ring: String,
    val vibration: Boolean,
    val autoDelete: Boolean,
    val remark: String,
    val enable: Boolean
)

class AlarmRepository {
   @Inject lateinit var applicationContext: Context
}