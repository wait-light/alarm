package com.example.alarmapplication.data

import kotlinx.coroutines.flow.Flow

interface AlarmDataSource {
    fun getAllAlarm(): Flow<List<Alarm>>
    suspend fun addAlarm(alarm: Alarm)
    suspend fun removeAlarm(alarmId: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
}