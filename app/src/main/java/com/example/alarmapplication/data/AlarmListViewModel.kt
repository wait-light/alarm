package com.example.alarmapplication.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmListViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    fun getAllAlarm(): Flow<List<Alarm>> = alarmRepository.getAllAlarm()
    suspend fun updateAlarm(alarm: Alarm) = alarmRepository.updateAlarm(alarm)
}