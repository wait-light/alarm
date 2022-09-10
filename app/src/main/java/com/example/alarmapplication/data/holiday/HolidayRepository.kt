package com.example.alarmapplication.data.holiday

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepository @Inject constructor() {
    @Inject
    lateinit var remoteHolidayDataSource: RemoteHolidayDataSource

    @Inject
    lateinit var localHolidayDataSource: LocalHolidayDataSource

    companion object {
        private const val TAG = "HolidayRepository"
    }

    //date format : yyyy-mm-dd
    suspend fun getHolidayStatus(date: String): HolidayStatus {
        var holidayStatus = localHolidayDataSource.getHolidayStatus(date)
        if (holidayStatus == null) {
            val yearHolidayStatus =
                remoteHolidayDataSource.getYearHolidayStatus(LocalDateTime.now().year)
            localHolidayDataSource.insetHolidayStatus(*yearHolidayStatus.toTypedArray())
            holidayStatus = localHolidayDataSource.getHolidayStatus(date)
        }
        return holidayStatus
    }

    suspend fun getTargetDayNextTargetWorkDay(
        targetDay: String,
        includeTargetToday: Boolean = true
    ) = getTargetDayNextTargetStatusDay(
        targetDay,
        includeTargetToday,
        null,
        HolidayStatus.STATUS_WORK_DAY,
        HolidayStatus.STATUS_SABBATICAL
    )

    suspend fun getTargetDayNextTargetStatusDay(
        targetDay: String,
        includeTargetToday: Boolean = true,
        judge: (HolidayStatus.() -> Boolean)?,
        @HolidayStatusDef vararg targetStatues: Int
    ): HolidayStatus {
        if (includeTargetToday) {
            getHolidayStatus(targetDay).let {
                if (targetStatues.contains(it.status) || (judge != null && it.judge())) {
                    return it
                }
            }
        }
        Log.d(TAG, "getTargetDayNextTargetStatusDay: ${targetDay}")
        var month = LocalDateTime.now().monthValue
        var year = LocalDateTime.now().year
        var isSkipped = false
        while (true) {
            var monthHolidayStatus = localHolidayDataSource.getMonthHolidayStatus(year, month)
            if (monthHolidayStatus.size == 0) {
                Log.d(TAG, "load date from remote and cache it")
                val yearHolidayStatus = remoteHolidayDataSource.getYearHolidayStatus(year)
                Log.d(TAG, "getTargetDayNextTargetStatusDay: $yearHolidayStatus")
                localHolidayDataSource.insetHolidayStatus(*yearHolidayStatus.toTypedArray())
                monthHolidayStatus = localHolidayDataSource.getMonthHolidayStatus(year, month)
                if (yearHolidayStatus.size == 0) {
                    month--
                    runBlocking {
                        delay(1000L)
                    }
                }
            }
            for (holiday in monthHolidayStatus) {
                if (isSkipped && (targetStatues.contains(holiday.status) || judge?.let { holiday.judge() } == true)) {
                    Log.d(TAG, "found: $holiday")
                    return holiday
                }
                if (holiday.date == targetDay) {
                    isSkipped = true
                }
            }
            month++
            if (month > 12) {
                month = 1
                year++
            }
            Log.d(TAG, "year $year month : $month")
        }
    }

    suspend fun getTargetDayNextHolidayOrWeekend(
        targetDay: String,
        includeTargetToday: Boolean = true
    ): HolidayStatus = getTargetDayNextTargetStatusDay(
        targetDay,
        includeTargetToday,
        null,
        HolidayStatus.STATUS_HOLIDAY,
        HolidayStatus.STATUS_WEEKEND
    )
}