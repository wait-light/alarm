package com.example.alarmapplication.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import javax.inject.Inject

class AlarmItemViewModel : ViewModel {
    @Inject
    lateinit var alarmRepository: AlarmRepository
    val isAddAlarm: LiveData<Boolean> = MutableLiveData(true)
    val currentAlarm: LiveData<Alarm> =
        MutableLiveData<Alarm>(
            Alarm(
                0,
                LocalTime.now(),
                AlarmRepeat.ONE_TIME,
                "",
                true,
                false,
                "",
                true
            )
        )

    @Inject
    constructor() : super()

    fun updateCurrentAlarm(alarm: Alarm) {
        (currentAlarm as MutableLiveData).value = alarm
    }

    suspend fun updateAlarm(alarm: Alarm) = alarmRepository.updateAlarm(alarm)

    fun setNotAddAlarm() {
        (isAddAlarm as MutableLiveData).value = false
    }

    suspend fun addAlarm(alarm: Alarm) = alarmRepository.addAlarm(alarm)
}