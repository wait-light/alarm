package com.example.alarmapplication.data

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.IntDef
import com.example.alarmapplication.data.holiday.HolidayRepository
import com.example.alarmapplication.data.holiday.HolidayStatus
import com.example.alarmapplication.domain.AlarmDomain
import com.example.alarmapplication.ui.AlarmListFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

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
    suspend fun nextTime(alarm: Alarm): Long
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

val ONE_MINUTE_MILLISECOND = 60 * 1000L
val ONE_HOUR_MILLISECOND = 60 * ONE_MINUTE_MILLISECOND
val ONE_DAY_MILLISECOND = 24 * ONE_HOUR_MILLISECOND
val ONE_DAY_MINUTE_SUM = 24 * 60

fun LocalDateTime.toMinuteSum(): Int {
    return hour * 60 + minute
}

fun LocalTime.toMinuteSum(): Int {
    return hour * 60 + minute
}

fun LocalTime.toEpochMilli(): Long {
    return this.atDate(LocalDate.now()).toEpochMilli()
}

fun LocalDateTime.toEpochMilli(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun digitsFill(number: Int, bit: Int): String {
    val between = bit - number.length()
    return "${"0".repeat(if (between > 0) between else 0)}${number}"
}


fun Long.userFriendlyTimeString(): String {
    val result = StringBuilder()
    var value = this
    val day = value / ONE_DAY_MILLISECOND
    if (day > 0) {
        value %= ONE_DAY_MILLISECOND
        result.append("${day}天")
    }
    val hour = value / ONE_HOUR_MILLISECOND
    if (hour > 0) {
        value %= ONE_HOUR_MILLISECOND
        result.append("${digitsFill(hour.toInt(), 2)}时")
    }
    val minite = value / ONE_MINUTE_MILLISECOND
    return result.append("${digitsFill(minite.toInt(), 2)}分钟").toString()
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
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context
    override fun onAlarm(alarm: Alarm) {
        if (alarm.autoDelete) {
            GlobalScope.launch {
                alarmDomain.removeAlarm(alarm)
                context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            }
        } else {
            alarm.enable = false
            GlobalScope.launch {
                alarmDomain.updateAlarm(alarm)
                context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            }
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        return if (nowEpochMilli <= deadlineEpochMilli)
            deadlineEpochMilli - nowEpochMilli
        else (ONE_DAY_MILLISECOND - (nowEpochMilli - deadlineEpochMilli))
    }
}

class Monday2FridayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context
    override fun onAlarm(alarm: Alarm) {
        GlobalScope.launch {
            context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            alarmDomain.startAlarm(alarm)
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        val nowWeek = now.dayOfWeek.value
        return if (nowEpochMilli <= deadlineEpochMilli && nowWeek <= 5)
            (deadlineEpochMilli - nowEpochMilli)
        else (ONE_DAY_MILLISECOND * (if (nowWeek <= 4) 1 else 8 - nowWeek) - (nowEpochMilli - deadlineEpochMilli))
    }
}

class WorkingDayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var holidyRepository: HolidayRepository
    override fun onAlarm(alarm: Alarm) {
        GlobalScope.launch {
            context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            alarmDomain.startAlarm(alarm)
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        return Math.abs(
            holidyRepository.getTargetDayNextTargetWorkDay(
                "${digitsFill(now.year,4)}-${digitsFill(now.monthValue,2)}-${digitsFill(now.dayOfMonth,2)}",
                nowEpochMilli < deadlineEpochMilli
            ).toLocalDateTime().withHour(alarm.localTime.hour)
                .withMinute(alarm.localTime.minute).toEpochMilli() - nowEpochMilli
        )
    }
}

class EverydayAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context
    override fun onAlarm(alarm: Alarm) {
        GlobalScope.launch {
            context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            alarmDomain.startAlarm(alarm)
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        return if (nowEpochMilli < deadlineEpochMilli)
            deadlineEpochMilli - nowEpochMilli
        else (ONE_DAY_MILLISECOND - (nowEpochMilli - deadlineEpochMilli))
    }
}

class StatutoryHolidaysAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var holidyRepository: HolidayRepository
    override fun onAlarm(alarm: Alarm) {
        GlobalScope.launch {
            context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            alarmDomain.startAlarm(alarm)
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        var result = 0L
        runBlocking {
            result = Math.abs(
                holidyRepository.getTargetDayNextHolidayOrWeekend(
                    "${digitsFill(now.year,4)}-${digitsFill(now.monthValue,2)}-${digitsFill(now.dayOfMonth,2)}",
                    nowEpochMilli < deadlineEpochMilli
                ).toLocalDateTime().withHour(alarm.localTime.hour)
                    .withMinute(alarm.localTime.minute).toEpochMilli() - nowEpochMilli
            )
        }
        return result
    }
}

class RewardAlarmRepeatStrategy @Inject constructor() : AlarmRepeatStrategy {
    @Inject
    lateinit var alarmDomain: AlarmDomain

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var holidyRepository: HolidayRepository
    override fun onAlarm(alarm: Alarm) {
        GlobalScope.launch {
            context.sendBroadcast(Intent(AlarmListFragment.DATA_UPDATE_KEY))
            alarmDomain.startAlarm(alarm)
        }
    }

    override suspend fun nextTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val deadline = alarm.localTime
        val nowEpochMilli = now.toEpochMilli()
        val deadlineEpochMilli = deadline.toEpochMilli()
        var result = 0L
        runBlocking {
            result = Math.abs(
                holidyRepository.getTargetDayNextTargetStatusDay(
                    "${digitsFill(now.year,4)}-${digitsFill(now.monthValue,2)}-${digitsFill(now.dayOfMonth,2)}",
                    nowEpochMilli < deadlineEpochMilli,
                    //大小周工作日判断
                    {
                        (this.status == HolidayStatus.STATUS_WORK_DAY || (isRewardWeek(
                            toLocalDateTime()
                        ) && toLocalDateTime().dayOfWeek.value == 6 && this.status == HolidayStatus.STATUS_WEEKEND)
                                || this.status == HolidayStatus.STATUS_SABBATICAL)
                    },
                ).toLocalDateTime().withHour(alarm.localTime.hour)
                    .withMinute(alarm.localTime.minute).toEpochMilli() - nowEpochMilli
            )
        }
        return result
    }

    private fun isRewardWeek(date: LocalDateTime): Boolean {
        return (((date.dayOfYear + 5) / 7) % 2) == 1
    }
}