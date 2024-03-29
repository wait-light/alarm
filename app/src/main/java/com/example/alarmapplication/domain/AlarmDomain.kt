package com.example.alarmapplication.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.alarmapplication.AlarmApplication
import com.example.alarmapplication.data.Alarm
import com.example.alarmapplication.data.AlarmReceiver
import com.example.alarmapplication.data.AlarmRepository
import com.example.alarmapplication.data.AlarmService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmDomain @Inject constructor() {
    @Inject
    lateinit var alarmRepository: AlarmRepository


    @Inject
    lateinit var applicationContext: Context

//    @Inject
//    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory


    suspend fun addAlarm(alarm: Alarm): Long {
        val alarmId = alarmRepository.addAlarm(alarm)
        if (alarm.enable) {
            startAlarm(alarm.apply { id = alarmId })
        }
        return alarmId
    }

    fun startAlarm(alarm: Alarm) {
        val alarmStrategy =
            AlarmApplication.ALARM_COMPONENT.getAlarmRepeatAlarmRepeatStrategyFactory()
                .getAlarmStrategy(alarm.repeat)
        val alarmService =
            applicationContext?.getSystemService(AlarmManager::class.java) as AlarmManager
        GlobalScope.launch {
            alarmService.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().apply {
                    timeInMillis += alarmStrategy!!.nextTime(alarm)
                }.timeInMillis,
                PendingIntent.getBroadcast(
                    applicationContext,
                    alarm.id.toInt(),
                    Intent(
                        applicationContext,
                        AlarmReceiver::class.java
                    ).apply {
                        setAction(AlarmService.ACTION_START)
                        putExtra(AlarmReceiver.ALARM_ID_KEY, alarm.id)
                        Log.e("TAG", "startAlarm: ${alarm.id}")
                    },
                    PendingIntent.FLAG_ONE_SHOT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
                )
            )
        }

    }

    private fun cancelAlarm(alarmId: Long) {
        val alarmService =
            applicationContext?.getSystemService(AlarmManager::class.java) as AlarmManager
        alarmService.cancel(
            PendingIntent.getBroadcast(
                applicationContext,
                alarmId.toInt(),
                Intent(
                    applicationContext,
                    AlarmReceiver::class.java
                ).apply {
                    setAction(AlarmService.ACTION_START)
                    putExtra(AlarmReceiver.ALARM_ID_KEY, alarmId)
                },
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_ONE_SHOT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0

            )
        )
    }

    suspend fun updateAlarm(alarm: Alarm) {
        if (alarm.id == 0L) {
            return
        }
        val oldValue = alarmRepository.getAlarm(alarm.id)
        alarmRepository.updateAlarm(alarm)
        //关闭闹钟
        if (!alarm.enable && alarm.enable != oldValue.enable) {
            cancelAlarm(alarm.id)
        }
        //开启闹钟
        if (alarm.enable && alarm.enable != oldValue.enable) {
            GlobalScope.launch {
                startAlarm(alarm)
            }
        }

    }

    suspend fun removeAlarm(alarm: Alarm) {
        if (alarm.id == 0L) {
            return
        }
        alarmRepository.removeAlarm(alarm)
        val alarmService =
            applicationContext?.getSystemService(AlarmManager::class.java) as AlarmManager
        alarmService.cancel(
            PendingIntent.getBroadcast(
                applicationContext,
                alarm.id.toInt(),
                Intent(
                    applicationContext,
                    AlarmReceiver::class.java
                ).apply {
                    setAction(AlarmService.ACTION_START)
                    putExtra(AlarmReceiver.ALARM_ID_KEY, alarm.id)
                },
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_ONE_SHOT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
            )
        )
    }


}