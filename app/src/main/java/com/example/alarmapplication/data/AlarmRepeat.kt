package com.example.alarmapplication.data

import androidx.annotation.IntDef
import javax.inject.Inject
import javax.inject.Singleton

@IntDef(
    AlarmRepeat.ONE_TIME,
    AlarmRepeat.MONDAY2FRIDAY,
    AlarmRepeat.WORKING_DAY,
    AlarmRepeat.EVERYDAY,
    AlarmRepeat.STATUTORY_HOLIDAYS,
    AlarmRepeat.REWARD
)
@Retention(AnnotationRetention.SOURCE)
annotation class AlarmRepeat {
    companion object {
        //一次
        const val ONE_TIME = 0

        //周一到周五
        const val MONDAY2FRIDAY = 1

        //工作日
        const val WORKING_DAY = 2

        //每天
        const val EVERYDAY = 3

        //法定节假日
        const val STATUTORY_HOLIDAYS = 4

        //大小周
        const val REWARD = 5
    }
}

interface AlarmRepeatStrategy {
    fun onAlarm(alarm: Alarm)
}

interface AlarmRepeatStrategyFactory {
    fun getAlarmStrategy(@AlarmRepeat alarmRepeat: Int)
}

class AlarmRepeatStrategyFactoryImpl @Inject constructor() :
    AlarmRepeatStrategyFactory {
    @Inject
    lateinit var strategies: Map<Int, @JvmSuppressWildcards AlarmRepeatStrategy>
    override fun getAlarmStrategy(alarmRepeat: Int) {
        TODO("Not yet implemented")
    }
}

class OneTimeAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

class Monday2FridayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

class WorkingDayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

class EverydayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

class StatutoryHolidaysAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}

class RewardAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }
}