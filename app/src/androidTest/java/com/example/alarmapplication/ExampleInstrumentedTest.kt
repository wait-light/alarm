package com.example.alarmapplication

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.alarmapplication.data.holiday.HolidayStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.time.LocalDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Before
    @After
    fun decorate() {
        println("---------------------------------------------")
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.alarmapplication", appContext.packageName)

    }

    @Test
    fun remoteHoliday() = runTest {
        val yearHolidayStatus =
            AlarmApplication.ALARM_COMPONENT.llala().getHolidayStatus("2022-09-10")
        println(yearHolidayStatus)
    }

    @Test
    fun nextHoliday() = runTest {
        val targetDayNextHolidayOrWeekend = AlarmApplication.ALARM_COMPONENT.llala()
            .getTargetDayNextHolidayOrWeekend("2022-09-12", true)
        println(targetDayNextHolidayOrWeekend)
    }

    @Test
    fun nextWorkDay() = runTest {
        val nextWorkDay = AlarmApplication.ALARM_COMPONENT.llala()
            .getTargetDayNextTargetStatusDay(
                "2022-09-12",
                true,
                null,
                HolidayStatus.STATUS_WORK_DAY,
                HolidayStatus.STATUS_SABBATICAL
            )
        println(nextWorkDay)
    }

    @Test
    fun nextRewardDay() = runTest {
        val targetDayNextTargetStatusDay =
            AlarmApplication.ALARM_COMPONENT.llala()
                .getTargetDayNextTargetStatusDay("2022-09-17", false,
                    {
                        (this.status == HolidayStatus.STATUS_WORK_DAY || (isRewardWeek(
                            toLocalDateTime()
                        ) && toLocalDateTime().dayOfWeek.value == 6 && this.status == HolidayStatus.STATUS_WEEKEND))
                    })
        assertEquals("2022-09-19", targetDayNextTargetStatusDay.date)
        assertEquals("2022-10-08", AlarmApplication.ALARM_COMPONENT.llala()
            .getTargetDayNextTargetStatusDay("2022-09-30", false,
                {
                    (this.status == HolidayStatus.STATUS_WORK_DAY || (isRewardWeek(
                        toLocalDateTime()
                    ) && toLocalDateTime().dayOfWeek.value == 6 && this.status == HolidayStatus.STATUS_WEEKEND) || this.status == HolidayStatus.STATUS_SABBATICAL)
                }).date.apply { println(this) }
        )
    }

    @Test
    fun daleyTest() = runTest {
        for (i in 1..10) {
            runBlocking {
                delay(1000L)
            }
            Log.e("$i", "daleyTest: ")
        }
    }

    private fun isRewardWeek(date: LocalDateTime): Boolean {
        return (((date.dayOfYear + 5) / 7) % 2) == 1
    }

}