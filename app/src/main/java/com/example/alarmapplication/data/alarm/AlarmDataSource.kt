package com.example.alarmapplication.data.alarm

import com.example.alarmapplication.data.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmDataSource {
    fun getAllAlarm(): Flow<List<Alarm>>
    fun getAlarm(id:Long): Alarm
    suspend fun addAlarm(alarm: Alarm):Long
    suspend fun removeAlarm(alarmId: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
}