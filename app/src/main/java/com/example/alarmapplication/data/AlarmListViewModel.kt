package com.example.alarmapplication.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AlarmListViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var alarmRepository: AlarmRepository
    val alarms: LiveData<List<Alarm>> = MutableLiveData(emptyList())
    var checkList: MutableList<Boolean> = mutableListOf()
        private set
    val multipleCheck: LiveData<Boolean> = MutableLiveData(false)
    fun toggleMultipleCheck() {
        if (!multipleCheck.value!!) {
            checkList = MutableList(alarms.value?.size ?: 0) { false }
        }
        (multipleCheck as MutableLiveData).value = !multipleCheck.value!!
    }

    suspend fun deletePickerAlarm() {
        val alarmList = alarms.value!!
        for ((index, check) in checkList.reversed().withIndex()) {
            if (check) {
                alarmRepository.removeAlarm(alarmList[index])
            }
        }
        (multipleCheck as MutableLiveData).postValue(!multipleCheck.value!!)
    }

    suspend fun fetchAllAlarm() {
        alarmRepository.getAllAlarm().collect {
            (alarms as MutableLiveData).postValue(it)
        }
    }

    suspend fun updateAlarm(alarm: Alarm) = alarmRepository.updateAlarm(alarm)
}