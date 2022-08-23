package com.example.alarmapplication.data

import android.util.Log
import androidx.annotation.IntDef
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.log

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

        val NAME_TYPE_PAIRS = listOf(
            "只响一次" to ONE_TIME,
            "周一到周五" to MONDAY2FRIDAY,
            "法定工作日" to WORKING_DAY,
            "每天" to EVERYDAY,
            "法定节假日" to STATUTORY_HOLIDAYS,
            "大小周上班时间" to REWARD
        )
    }
}

interface AlarmRepeatStrategy {
    fun onAlarm(alarm: Alarm)
    fun nextTime(alarm: Alarm): String
}

interface AlarmRepeatStrategyFactory {
    fun getAlarmStrategy(@AlarmRepeat alarmRepeat: Int): AlarmRepeatStrategy?
}

class AlarmRepeatStrategyFactoryImpl @Inject constructor() :
    AlarmRepeatStrategyFactory {
    @Inject
    lateinit var strategies: Map<Int, @JvmSuppressWildcards AlarmRepeatStrategy>
    override fun getAlarmStrategy(alarmRepeat: Int): AlarmRepeatStrategy? {
        return strategies[alarmRepeat]
    }
}

val ONE_MINUTE_SUM = 24 * 60

fun LocalDateTime.toMinuteSum(): Int {
    return hour * 60 + minute
}

fun LocalTime.toMinuteSum(): Int {
    return hour * 60 + minute
}

fun digitsFill(number: Int, bit: Int): String {
    val between = bit - number.length()
    return "${"0".repeat(if (between > 0) between else 0)}${number}"
}

fun Int.length(): Int {
    if (this < 0) {
        throw UnsupportedOperationException("only support positive number")
    }
    if (this == 0) {
        return 1
    }
    var it = this
    var bit = 0
    while (it > 0) {
        bit++
        it /= 10
    }
    return bit
}

class OneTimeAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowMinuteSum = now.toMinuteSum()
        val deadlineSum = deadline.toMinuteSum()
        val result = StringBuilder()
        if (nowMinuteSum < deadlineSum) {
            val hour = (deadlineSum - nowMinuteSum) / 60
            if (hour > 0) {
                result.append("${digitsFill(hour, 2)}时")
            }
            result.append("${digitsFill((deadlineSum - nowMinuteSum) % 60, 2)}分钟")
        } else {
            val betweenMinute = (ONE_MINUTE_SUM - (nowMinuteSum - deadlineSum))
            val hour = betweenMinute / 60
            if (hour > 0) {
                result.append("${digitsFill(hour, 2)}时")
            }
            result.append("${digitsFill(betweenMinute % 60, 2)}分钟")
        }
        return result.toString()
    }
}

class Monday2FridayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        TODO("Not yet implemented")
    }
}

class WorkingDayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        TODO("Not yet implemented")
    }
}

class EverydayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        TODO("Not yet implemented")
    }
}

class StatutoryHolidaysAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        TODO("Not yet implemented")
    }
}

class RewardAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    override fun onAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun nextTime(alarm: Alarm): String {
        TODO("Not yet implemented")
    }
}