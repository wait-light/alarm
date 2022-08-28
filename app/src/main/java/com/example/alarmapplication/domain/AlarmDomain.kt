package com.example.alarmapplication.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmapplication.data.Alarm
import com.example.alarmapplication.data.AlarmReceiver
import com.example.alarmapplication.data.AlarmRepeatStrategyFactory
import com.example.alarmapplication.data.AlarmRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class AlarmDataWithAlarmManager @Inject constructor() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var applicationContext: Context
    suspend fun updateAlarm(alarm: Alarm) {
        if (alarm.id == 0L) {
            return
        }
        alarmRepository.updateAlarm(alarm)
        if (!alarm.enable) {
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
                        setAction(AlarmReceiver.ACTION_START)
                        putExtra(AlarmReceiver.ALARM_ID_KEY, alarm.id)
                    },
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
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
                    setAction(AlarmReceiver.ACTION_START)
                    putExtra(AlarmReceiver.ALARM_ID_KEY, alarm.id)
                },
                PendingIntent.FLAG_ONE_SHOT
            )
        )
    }
}

@Singleton
class AlarmDomain @Inject constructor() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmDataWithAlarmManager: AlarmDataWithAlarmManager

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var alarmRepeatStrategyFactory: AlarmRepeatStrategyFactory

    suspend fun updateAlarm(alarm: Alarm) = alarmDataWithAlarmManager.updateAlarm(alarm)
    suspend fun removeAlarm(alarm: Alarm) = alarmDataWithAlarmManager.removeAlarm(alarm)

    suspend fun addAlarm(alarm: Alarm): Long {
        return alarmRepository.addAlarm(alarm).apply {
            if (!alarm.enable) {
                return this
            }
            alarm.id = this
            val alarmStrategy = alarmRepeatStrategyFactory.getAlarmStrategy(alarm.repeat)
            val alarmService =
                applicationContext?.getSystemService(AlarmManager::class.java) as AlarmManager
            alarmService.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().apply {
                    timeInMillis += alarmStrategy!!.nextTime(alarm)
                }.timeInMillis,
                PendingIntent.getBroadcast(
                    applicationContext,
                    this.toInt(),
                    Intent(
                        applicationContext,
                        AlarmReceiver::class.java
                    ).apply {
                        setAction(AlarmReceiver.ACTION_START)
                        putExtra(AlarmReceiver.ALARM_ID_KEY, alarm.id)
                    },
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
        }
    }


}